package com.nst.myvehiclehub.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.nst.myvehiclehub.dto.request.*;
import com.nst.myvehiclehub.dto.response.LoginResponse;
import com.nst.myvehiclehub.dto.response.RefreshTokenResponse;
import com.nst.myvehiclehub.dto.response.RegisterResponseDTO;
import com.nst.myvehiclehub.entity.AppUserRecord;

public interface AuthService {
  RegisterResponseDTO register(RegisterRequestDTO registerRequestDTO);

  LoginResponse login(LoginRequestDTO loginRequestDTO);

  LoginResponse googleLogin(GoogleLoginRequestDTO request);

  RefreshTokenResponse refreshToken(RefreshTokenDTO request);

  void logout(RefreshTokenDTO request);

  void logoutAll(AppUserRecord user);

  void validateGoogleToken(String idToken);

  AppUserRecord handleGoogleUser(GoogleIdToken.Payload payload);
}
