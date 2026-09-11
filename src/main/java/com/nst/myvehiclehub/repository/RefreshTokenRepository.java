package com.nst.myvehiclehub.repository;

import com.nst.myvehiclehub.entity.AppUserRecord;
import com.nst.myvehiclehub.entity.RefreshTokenRecord;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenRecord, Long> {

  // Find token for validation during refresh
  Optional<RefreshTokenRecord> findByToken(String token);

  // Find all tokens for a user (for viewing active sessions/devices)
  List<RefreshTokenRecord> findByUser(AppUserRecord user);

  // Delete expired tokens (for automatic cleanup job)
  @Modifying
  @Query("DELETE FROM RefreshTokenRecord r WHERE r.expiryDate < :date")
  void deleteByExpiryDateBefore(@Param("date") Instant date);

  // Optional: Keep for emergency use (compromised account, password change)
  @Modifying
  @Query("DELETE FROM RefreshTokenRecord r WHERE r.user = :user")
  void deleteByUser(@Param("user") AppUserRecord user);
}
