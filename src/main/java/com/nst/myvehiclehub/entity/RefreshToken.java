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
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String token;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private AppUser user;

  @Column(nullable = false)
  private Instant expiryDate;

  @Column(nullable = false)
  private Instant createdDate;

  @Column(nullable = false)
  private boolean revoked = false;
}
