package com.nst.myvehiclehub.controller;

import com.nst.myvehiclehub.dto.request.GoogleLoginRequestDTO;
import com.nst.myvehiclehub.dto.request.LoginRequestDTO;
import com.nst.myvehiclehub.dto.request.RefreshTokenDTO;
import com.nst.myvehiclehub.dto.request.RegisterRequestDTO;
import com.nst.myvehiclehub.dto.response.LoginResponse;
import com.nst.myvehiclehub.dto.response.RefreshTokenResponse;
import com.nst.myvehiclehub.dto.response.RegisterResponse;
import com.nst.myvehiclehub.entity.UserPrincipalRecord;
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
  public ResponseEntity<RegisterResponse> register(
      @RequestBody RegisterRequestDTO registerRequestDTO) {
    return ResponseEntity.ok(authService.register(registerRequestDTO));
  }

  @PostMapping(
      path = "/login",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequestDTO loginRequestDTO) {
    return ResponseEntity.ok(authService.login(loginRequestDTO));
  }

  @PostMapping(
      path = "/google",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<LoginResponse> googleLogin(@RequestBody GoogleLoginRequestDTO request) {
    return ResponseEntity.ok(authService.googleLogin(request));
  }

  @PostMapping(
      path = "/refreshToken",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenDTO request) {
    return ResponseEntity.ok(authService.refreshToken(request));
  }

  @PostMapping(
      path = "/logout",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> logout(@RequestBody RefreshTokenDTO request) {
    authService.logout(request);
    return ResponseEntity.ok("Logged out successfully");
  }

  @PostMapping(
      path = "/logout-all",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> logoutAll(Authentication authentication) {
    UserPrincipalRecord userPrincipal = (UserPrincipalRecord) authentication.getPrincipal();
    authService.logoutAll(userPrincipal.getUser());
    return ResponseEntity.ok("Logged out from all devices successfully");
  }
}
