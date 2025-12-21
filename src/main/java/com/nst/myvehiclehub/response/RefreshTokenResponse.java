package com.nst.myvehiclehub.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefreshTokenResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn; // in seconds
}

