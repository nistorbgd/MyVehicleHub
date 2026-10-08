package com.nst.myvehiclehub.entity;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(
    name = "refresh_tokens",
    indexes = {@Index(name = "idx_user_id", columnList = "user_id")})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRecord {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "token", nullable = false, unique = true)
  private String token;

  @Column(name = "expiryDate", nullable = false)
  private Instant expiryDate;

  @Column(name = "createdDate", nullable = false)
  private Instant createdDate;

  @Column(name = "revoked", nullable = false)
  private boolean revoked = false;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private AppUserRecord user;
}
