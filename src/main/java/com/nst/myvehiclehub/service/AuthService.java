package com.nst.myvehiclehub.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.nst.myvehiclehub.entity.AppUser;
import com.nst.myvehiclehub.entity.AuthProvider;
import com.nst.myvehiclehub.entity.RefreshToken;
import com.nst.myvehiclehub.entity.Role;
import com.nst.myvehiclehub.entity.UserPrincipal;
import com.nst.myvehiclehub.exception.RefreshTokenNotFoundException;
import com.nst.myvehiclehub.repository.AppUserRepository;
import com.nst.myvehiclehub.request.*;
import com.nst.myvehiclehub.response.LoginResponse;
import com.nst.myvehiclehub.response.RefreshTokenResponse;
import com.nst.myvehiclehub.response.RegisterResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

  private final AppUserRepository appUserRepository;
  private final AuthenticationManager authManager;
  private final JWTService jwtService;
  private final RefreshTokenService refreshTokenService;
  private final BCryptPasswordEncoder passwordEncoder;

  @Value("${spring.security.oauth2.client.registration.google.client-id}")
  private String googleClientId;

  public AuthService(
      AppUserRepository appUserRepository,
      AuthenticationManager authenticationManager,
      JWTService jwtService,
      RefreshTokenService refreshTokenService,
      BCryptPasswordEncoder passwordEncoder) {
    this.appUserRepository = appUserRepository;
    this.authManager = authenticationManager;
    this.jwtService = jwtService;
    this.refreshTokenService = refreshTokenService;
    this.passwordEncoder = passwordEncoder;
  }

  public RegisterResponse register(RegisterRequest registerRequest) {
    validateRegisterRequest(registerRequest);
    AppUser newUser =
        AppUser.builder()
            .email(registerRequest.getEmail())
            .password(passwordEncoder.encode(registerRequest.getPassword()))
            .lastName(registerRequest.getLastName())
            .firstName(registerRequest.getFirstName())
            .age(registerRequest.getAge())
            .role(Role.USER)
            .authProvider(AuthProvider.EMAIL)
            .build();

    appUserRepository.save(newUser);
    return new RegisterResponse(newUser.getId().toString());
  }

  private void validateRegisterRequest(RegisterRequest registerRequest) {
    if (registerRequest.getEmail() == null || registerRequest.getPassword() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email or password is missing");
    }

    if (appUserRepository.existsByEmail(registerRequest.getEmail())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use");
    }
  }

  public LoginResponse login(LoginRequest loginRequest) {
    validateLoginRequest(loginRequest);
    Authentication authentication =
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword()));

    if (authentication.isAuthenticated()) {
      UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
      AppUser user = userPrincipal.getUser();
      String accessToken = jwtService.generateToken(loginRequest.getEmail());
      String refreshToken = refreshTokenService.createRefreshToken(user).getToken();
      return new LoginResponse(accessToken, refreshToken);
    }

    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
  }

  private void validateLoginRequest(LoginRequest loginRequest) {
    if (loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email or password is missing");
    }
  }

  public LoginResponse googleLogin(GoogleLoginRequest request) {
    validateGoogleToken(request.getIdToken());
    try {
      GoogleIdTokenVerifier verifier =
          new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
              .setAudience(Collections.singletonList(googleClientId))
              .build();

      GoogleIdToken idToken = verifier.verify(request.getIdToken());
      if (idToken == null) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Google ID token");
      }

      GoogleIdToken.Payload payload = idToken.getPayload();
      AppUser user = handleGoogleUser(payload);
      String accessToken = jwtService.generateToken(user.getEmail());
      String refreshToken = refreshTokenService.createRefreshToken(user).getToken();

      return new LoginResponse(accessToken, refreshToken);

    } catch (GeneralSecurityException | IOException e) {
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, "Failed to verify Google token", e);
    }
  }

  public void validateGoogleToken(String idToken) {
    if (idToken == null || idToken.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID token is required");
    }
  }

  public AppUser handleGoogleUser(GoogleIdToken.Payload payload) {
    String email = payload.getEmail();
    String firstName = (String) payload.get("given_name");
    String lastName = (String) payload.get("family_name");

    return appUserRepository
        .findByEmail(email)
        .orElseGet(
            () -> {
              AppUser newUser =
                  AppUser.builder()
                      .email(email)
                      .firstName(firstName)
                      .lastName(lastName)
                      .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                      .role(Role.USER)
                      .authProvider(AuthProvider.GOOGLE)
                      .build();
              return appUserRepository.save(newUser);
            });
  }

  @Transactional
  public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
    validateRefreshTokenRequest(request);
    RefreshToken refreshToken =
        refreshTokenService
            .findByToken(request.getRefreshToken())
            .orElseThrow(
                () -> new RefreshTokenNotFoundException("Session expired. Please login again."));
    refreshTokenService.verifyExpiration(refreshToken);

    AppUser user = refreshToken.getUser();
    String newJwtToken = jwtService.generateToken(user.getEmail());
    refreshTokenService.revokeToken(refreshToken);
    String newRefreshToken = refreshTokenService.createRefreshToken(user).getToken();
    return new RefreshTokenResponse(newJwtToken, newRefreshToken);
  }

  private static void validateRefreshTokenRequest(RefreshTokenRequest request) {
    if (request.getRefreshToken() == null || request.getRefreshToken().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh token is required");
    }
  }

  public void logout(LogoutRequest request) {
    validateLogoutRequest(request);
    RefreshToken refreshToken =
        refreshTokenService
            .findByToken(request.getRefreshToken())
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Refresh token not found"));
    refreshTokenService.revokeToken(refreshToken);
  }

  private static void validateLogoutRequest(LogoutRequest request) {
    if (request.getRefreshToken() == null || request.getRefreshToken().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh token is required");
    }
  }

  public void logoutAll(AppUser user) {
    refreshTokenService.revokeAllUserTokens(user);
  }
}
