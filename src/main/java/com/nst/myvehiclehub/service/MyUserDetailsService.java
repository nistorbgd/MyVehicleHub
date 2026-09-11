package com.nst.myvehiclehub.service;

import com.nst.myvehiclehub.entity.AppUser;
import com.nst.myvehiclehub.entity.UserPrincipal;
import com.nst.myvehiclehub.repository.AppUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

  private final AppUserRepository appUserRepository;

  public MyUserDetailsService(AppUserRepository appUserRepository) {
    this.appUserRepository = appUserRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    AppUser user =
        appUserRepository
            .findByEmail(email)
            .orElseThrow(
                () -> new UsernameNotFoundException("User not found with email: " + email));

    return new UserPrincipal(user);
  }
}
