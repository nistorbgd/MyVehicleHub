package com.nst.myvehiclehub.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.nst.myvehiclehub.dto.request.*;
import com.nst.myvehiclehub.dto.response.LoginResponse;
import com.nst.myvehiclehub.dto.response.RefreshTokenResponse;
import com.nst.myvehiclehub.dto.response.RegisterResponse;
import com.nst.myvehiclehub.entity.AppUser;

public interface AuthService {
  RegisterResponse register(RegisterRequestDTO registerRequestDTO);

  LoginResponse login(LoginRequestDTO loginRequestDTO);

  LoginResponse googleLogin(GoogleLoginRequestDTO request);

  RefreshTokenResponse refreshToken(RefreshTokenDTO request);

  void logout(RefreshTokenDTO request);

  void logoutAll(AppUser user);

  void validateGoogleToken(String idToken);

  AppUser handleGoogleUser(GoogleIdToken.Payload payload);
}
