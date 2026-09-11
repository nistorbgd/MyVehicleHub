package com.nst.myvehiclehub.serviceImpl;

import com.nst.myvehiclehub.entity.AppUserRecord;
import com.nst.myvehiclehub.entity.UserPrincipalRecord;
import com.nst.myvehiclehub.repository.AppUserRepository;
import com.nst.myvehiclehub.service.MyUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsServiceImpl implements MyUserDetailsService {

  private final AppUserRepository appUserRepository;

  public MyUserDetailsServiceImpl(AppUserRepository appUserRepository) {
    this.appUserRepository = appUserRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    AppUserRecord user =
        appUserRepository
            .findByEmail(email)
            .orElseThrow(
                () -> new UsernameNotFoundException("User not found with email: " + email));

    return new UserPrincipalRecord(user);
  }
}
