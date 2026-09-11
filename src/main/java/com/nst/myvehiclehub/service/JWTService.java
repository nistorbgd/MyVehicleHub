package com.nst.myvehiclehub.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JWTService {
  String generateToken(String email);

  String extractUserName(String token);

  boolean validateToken(String token, UserDetails userDetails);
}
