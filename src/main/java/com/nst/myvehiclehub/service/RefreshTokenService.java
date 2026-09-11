package com.nst.myvehiclehub.service;

import com.nst.myvehiclehub.entity.AppUser;
import com.nst.myvehiclehub.entity.RefreshToken;
import java.util.Optional;

public interface RefreshTokenService {
  RefreshToken createRefreshToken(AppUser user);

  RefreshToken verifyExpiration(RefreshToken token);

  Optional<RefreshToken> findByToken(String token);

  void revokeToken(RefreshToken token);

  void revokeAllUserTokens(AppUser user);

  void deleteExpiredTokens();
}
