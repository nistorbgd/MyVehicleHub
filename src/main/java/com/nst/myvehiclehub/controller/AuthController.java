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
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping(
      path = "/register",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest) {
    return ResponseEntity.ok(authService.register(registerRequest));
  }

  @PostMapping(
      path = "/login",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
    return ResponseEntity.ok(authService.login(loginRequest));
  }

  @PostMapping(
      path = "/google",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<LoginResponse> googleLogin(@RequestBody GoogleLoginRequest request) {
    return ResponseEntity.ok(authService.googleLogin(request));
  }

  @PostMapping(
      path = "/refreshToken",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<RefreshTokenResponse> refreshToken(
      @RequestBody RefreshTokenRequest request) {
    return ResponseEntity.ok(authService.refreshToken(request));
  }

  @PostMapping(
      path = "/logout",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> logout(@RequestBody LogoutRequest request) {
    authService.logout(request);
    return ResponseEntity.ok("Logged out successfully");
  }

  @PostMapping(
      path = "/logout-all",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> logoutAll(Authentication authentication) {
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    authService.logoutAll(userPrincipal.getUser());
    return ResponseEntity.ok("Logged out from all devices successfully");
  }
}
