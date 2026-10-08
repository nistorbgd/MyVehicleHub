package com.nst.myvehiclehub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefreshTokenResponse {
  private String jwtToken;
  private String refreshToken;
}
