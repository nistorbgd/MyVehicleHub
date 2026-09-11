package com.nst.myvehiclehub.controller;

import com.nst.myvehiclehub.entity.UserPrincipal;
import com.nst.myvehiclehub.request.GoogleLoginRequest;
import com.nst.myvehiclehub.request.LoginRequest;
import com.nst.myvehiclehub.request.LogoutRequest;
import com.nst.myvehiclehub.request.RefreshTokenRequest;
import com.nst.myvehiclehub.request.RegisterRequest;
import com.nst.myvehiclehub.response.LoginResponse;
import com.nst.myvehiclehub.response.RefreshTokenResponse;
import com.nst.myvehiclehub.response.RegisterResponse;
import com.nst.myvehiclehub.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public RegisterResponse register(@RequestBody RegisterRequest registerRequest) {
    return authService.register(registerRequest);
  }

  @PostMapping("/login")
  public LoginResponse login(@RequestBody LoginRequest loginRequest) {
    return authService.login(loginRequest);
  }

  @PostMapping("/google")
  public LoginResponse googleLogin(@RequestBody GoogleLoginRequest request) {
    return authService.googleLogin(request);
  }

  @PostMapping("/refreshToken")
  public RefreshTokenResponse refreshToken(@RequestBody RefreshTokenRequest request) {
    return authService.refreshToken(request);
  }

  @PostMapping("/logout")
  public ResponseEntity<String> logout(@RequestBody LogoutRequest request) {
    authService.logout(request);
    return ResponseEntity.ok("Logged out successfully");
  }

  @PostMapping("/logout-all")
  public ResponseEntity<String> logoutAll(Authentication authentication) {
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    authService.logoutAll(userPrincipal.getUser());
    return ResponseEntity.ok("Logged out from all devices successfully");
  }
}
