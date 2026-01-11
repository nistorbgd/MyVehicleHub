package com.nst.myvehiclehub.service;

import com.nst.myvehiclehub.entity.AppUser;
import com.nst.myvehiclehub.entity.RefreshToken;
import com.nst.myvehiclehub.exception.ExpiredRefreshTokenException;
import com.nst.myvehiclehub.repository.AppUserRepository;
import com.nst.myvehiclehub.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AppUserRepository appUserRepository;

    @Value("${app.refresh-token.expiration}")
    private long expirationDuration;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, AppUserRepository appUserRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.appUserRepository = appUserRepository;
    }

    public RefreshToken createRefreshToken(AppUser user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(java.util.UUID.randomUUID().toString())
                .user(user)
                .createdDate(Instant.now())
                .expiryDate(Instant.now().plusSeconds(expirationDuration))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new ExpiredRefreshTokenException(
                "Refresh token expired. Please login again."
            );
        }
        return token;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public void revokeToken(RefreshToken token) {
        refreshTokenRepository.delete(token);
    }

    @Transactional
    public void revokeAllUserTokens(AppUser user) {
        refreshTokenRepository.deleteByUser(user);
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?") // Runs daily at midnight
    public void deleteExpiredTokens() {
        refreshTokenRepository.deleteByExpiryDateBefore(Instant.now());
    }
}
