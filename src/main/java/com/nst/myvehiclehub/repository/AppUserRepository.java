package com.nst.myvehiclehub.repository;

import com.nst.myvehiclehub.entity.AppUserRecord;
import com.nst.myvehiclehub.enums.AuthProvider;
import com.nst.myvehiclehub.enums.Role;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUserRecord, UUID> {

  boolean existsByEmail(String email);

  Optional<AppUserRecord> findByEmail(String email);

  List<AppUserRecord> findByRole(Role role);

  List<AppUserRecord> findByAuthProvider(AuthProvider authProvider);
}
