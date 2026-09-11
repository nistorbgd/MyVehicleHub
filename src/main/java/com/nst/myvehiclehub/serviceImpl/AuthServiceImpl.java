package com.nst.myvehiclehub.serviceImpl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.nst.myvehiclehub.dto.request.*;
import com.nst.myvehiclehub.dto.response.LoginResponse;
import com.nst.myvehiclehub.dto.response.RefreshTokenResponse;
import com.nst.myvehiclehub.dto.response.RegisterResponse;
import com.nst.myvehiclehub.entity.AppUser;
import com.nst.myvehiclehub.entity.RefreshToken;
import com.nst.myvehiclehub.entity.UserPrincipal;
import com.nst.myvehiclehub.enums.AuthProvider;
import com.nst.myvehiclehub.enums.Role;
import com.nst.myvehiclehub.exception.RefreshTokenNotFoundException;
import com.nst.myvehiclehub.repository.AppUserRepository;
import com.nst.myvehiclehub.service.AuthService;
import com.nst.myvehiclehub.service.JWTService;
import com.nst.myvehiclehub.service.RefreshTokenService;
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
public class AuthServiceImpl implements AuthService {

  private final AppUserRepository appUserRepository;
  private final AuthenticationManager authManager;
  private final JWTService jwtService;
  private final RefreshTokenService refreshTokenService;
  private final BCryptPasswordEncoder passwordEncoder;

  @Value("${spring.security.oauth2.client.registration.google.client-id}")
  private String googleClientId;

  public AuthServiceImpl(
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

  public RegisterResponse register(RegisterRequestDTO registerRequestDTO) {
    validateRegisterRequest(registerRequestDTO);
    AppUser newUser =
        AppUser.builder()
            .email(registerRequestDTO.getEmail())
            .password(passwordEncoder.encode(registerRequestDTO.getPassword()))
            .lastName(registerRequestDTO.getLastName())
            .firstName(registerRequestDTO.getFirstName())
            .age(registerRequestDTO.getAge())
            .role(Role.USER)
            .authProvider(AuthProvider.EMAIL)
            .build();

    appUserRepository.save(newUser);
    return new RegisterResponse(newUser.getId().toString());
  }

  private void validateRegisterRequest(RegisterRequestDTO registerRequestDTO) {
    if (registerRequestDTO.getEmail() == null || registerRequestDTO.getPassword() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email or password is missing");
    }

    if (appUserRepository.existsByEmail(registerRequestDTO.getEmail())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use");
    }
  }

  public LoginResponse login(LoginRequestDTO loginRequestDTO) {
    validateLoginRequest(loginRequestDTO);
    Authentication authentication =
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequestDTO.getEmail(), loginRequestDTO.getPassword()));

    if (authentication.isAuthenticated()) {
      UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
      AppUser user = userPrincipal.getUser();
      String accessToken = jwtService.generateToken(loginRequestDTO.getEmail());
      String refreshToken = refreshTokenService.createRefreshToken(user).getToken();
      return new LoginResponse(accessToken, refreshToken);
    }

    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
  }

  private void validateLoginRequest(LoginRequestDTO loginRequestDTO) {
    if (loginRequestDTO.getEmail() == null || loginRequestDTO.getPassword() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email or password is missing");
    }
  }

  public LoginResponse googleLogin(GoogleLoginRequestDTO request) {
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
  public RefreshTokenResponse refreshToken(RefreshTokenDTO request) {
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

  private static void validateRefreshTokenRequest(RefreshTokenDTO request) {
    if (request.getRefreshToken() == null || request.getRefreshToken().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh token is required");
    }
  }

  public void logout(RefreshTokenDTO request) {
    validateLogoutRequest(request);
    RefreshToken refreshToken =
        refreshTokenService
            .findByToken(request.getRefreshToken())
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Refresh token not found"));
    refreshTokenService.revokeToken(refreshToken);
  }

  private static void validateLogoutRequest(RefreshTokenDTO request) {
    if (request.getRefreshToken() == null || request.getRefreshToken().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh token is required");
    }
  }

  public void logoutAll(AppUser user) {
    refreshTokenService.revokeAllUserTokens(user);
  }
}
