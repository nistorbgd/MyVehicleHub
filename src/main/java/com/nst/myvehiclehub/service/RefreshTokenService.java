package com.nst.myvehiclehub.service;

import com.nst.myvehiclehub.entity.AppUserRecord;
import com.nst.myvehiclehub.entity.RefreshTokenRecord;
import java.util.Optional;

public interface RefreshTokenService {
  RefreshTokenRecord createRefreshToken(AppUserRecord user);

  RefreshTokenRecord verifyExpiration(RefreshTokenRecord token);

  Optional<RefreshTokenRecord> findByToken(String token);

  void revokeToken(RefreshTokenRecord token);

  void revokeAllUserTokens(AppUserRecord user);

  void deleteExpiredTokens();
}
