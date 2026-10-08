package com.nst.myvehiclehub.entity;

import com.nst.myvehiclehub.enums.AuthProvider;
import com.nst.myvehiclehub.enums.Role;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUserRecord {

  @Id
  @Column(name = "id")
  @UuidGenerator
  private UUID id;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "lastName", nullable = false)
  private String lastName;

  @Column(name = "firstName", nullable = false)
  private String firstName;

  @Column(name = "age", nullable = false)
  private int age;

  @OneToMany(mappedBy = "user")
  private List<VehicleRecord> vehicles;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  @Builder.Default
  private Role role = Role.USER;

  @Enumerated(EnumType.STRING)
  @Column(name = "authProvider", nullable = false)
  private AuthProvider authProvider;
}
