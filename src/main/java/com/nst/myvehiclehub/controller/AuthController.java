package com.nst.myvehiclehub.controller;

import com.nst.myvehiclehub.dto.request.GoogleLoginRequest;
import com.nst.myvehiclehub.dto.request.LoginRequest;
import com.nst.myvehiclehub.dto.request.LogoutRequest;
import com.nst.myvehiclehub.dto.request.RefreshTokenRequest;
import com.nst.myvehiclehub.dto.request.RegisterRequest;
import com.nst.myvehiclehub.dto.response.LoginResponse;
import com.nst.myvehiclehub.dto.response.RefreshTokenResponse;
import com.nst.myvehiclehub.dto.response.RegisterResponse;
import com.nst.myvehiclehub.entity.UserPrincipal;
import com.nst.myvehiclehub.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService authServiceImpl;

  public AuthController(AuthService authServiceImpl) {
    this.authServiceImpl = authServiceImpl;
  }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public RegisterResponse register(@RequestBody RegisterRequest registerRequest) {
    return authServiceImpl.register(registerRequest);
  }

  @PostMapping("/login")
  public LoginResponse login(@RequestBody LoginRequest loginRequest) {
    return authServiceImpl.login(loginRequest);
  }

  @PostMapping("/google")
  public LoginResponse googleLogin(@RequestBody GoogleLoginRequest request) {
    return authServiceImpl.googleLogin(request);
  }

  @PostMapping("/refreshToken")
  public RefreshTokenResponse refreshToken(@RequestBody RefreshTokenRequest request) {
    return authServiceImpl.refreshToken(request);
  }

  @PostMapping("/logout")
  public ResponseEntity<String> logout(@RequestBody LogoutRequest request) {
    authServiceImpl.logout(request);
    return ResponseEntity.ok("Logged out successfully");
  }

  @PostMapping("/logout-all")
  public ResponseEntity<String> logoutAll(Authentication authentication) {
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    authServiceImpl.logoutAll(userPrincipal.getUser());
    return ResponseEntity.ok("Logged out from all devices successfully");
  }
}
