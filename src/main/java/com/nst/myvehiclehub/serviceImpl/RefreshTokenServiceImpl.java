package com.nst.myvehiclehub.serviceImpl;

import com.nst.myvehiclehub.entity.AppUserRecord;
import com.nst.myvehiclehub.entity.RefreshTokenRecord;
import com.nst.myvehiclehub.exception.ExpiredRefreshTokenException;
import com.nst.myvehiclehub.repository.RefreshTokenRepository;
import com.nst.myvehiclehub.service.RefreshTokenService;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;

  @Value("${app.refresh-token.expiration}")
  private long expirationDuration;

  public RefreshTokenRecord createRefreshToken(AppUserRecord user) {
    RefreshTokenRecord refreshToken =
        RefreshTokenRecord.builder()
            .token(java.util.UUID.randomUUID().toString())
            .user(user)
            .createdDate(Instant.now())
            .expiryDate(Instant.now().plusSeconds(expirationDuration))
            .revoked(false)
            .build();

    return refreshTokenRepository.save(refreshToken);
  }

  public RefreshTokenRecord verifyExpiration(RefreshTokenRecord token) {
    if (token.getExpiryDate().isBefore(Instant.now())) {
      refreshTokenRepository.delete(token);
      throw new ExpiredRefreshTokenException("Refresh token expired. Please login again.");
    }
    return token;
  }

  public Optional<RefreshTokenRecord> findByToken(String token) {
    return refreshTokenRepository.findByToken(token);
  }

  @Transactional
  public void revokeToken(RefreshTokenRecord token) {
    refreshTokenRepository.delete(token);
  }

  @Transactional
  public void revokeAllUserTokens(AppUserRecord user) {
    refreshTokenRepository.deleteByUser(user);
  }

  @Transactional
  @Scheduled(cron = "0 0 0 * * ?") // Runs daily at midnight
  public void deleteExpiredTokens() {
    refreshTokenRepository.deleteByExpiryDateBefore(Instant.now());
  }
}
