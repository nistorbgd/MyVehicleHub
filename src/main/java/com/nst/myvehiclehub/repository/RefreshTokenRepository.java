package com.nst.myvehiclehub.repository;

import com.nst.myvehiclehub.entity.AppUser;
import com.nst.myvehiclehub.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // Find token for validation during refresh
    Optional<RefreshToken> findByToken(String token);

    // Find all tokens for a user (for viewing active sessions/devices)
    List<RefreshToken> findByUser(AppUser user);

    // Delete expired tokens (for automatic cleanup job)
    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.expiryDate < :date")
    void deleteByExpiryDateBefore(@Param("date") Instant date);

    // Optional: Keep for emergency use (compromised account, password change)
    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.user = :user")
    void deleteByUser(@Param("user") AppUser user);
}
