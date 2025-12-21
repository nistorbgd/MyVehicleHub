package com.nst.myvehiclehub.repository;

import com.nst.myvehiclehub.entity.AppUser;
import com.nst.myvehiclehub.entity.AuthProvider;
import com.nst.myvehiclehub.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {

    boolean existsByEmail(String email);

    Optional<AppUser> findByEmail(String email);

    List<AppUser> findByRole(Role role);

    List<AppUser> findByAuthProvider(AuthProvider authProvider);
}
