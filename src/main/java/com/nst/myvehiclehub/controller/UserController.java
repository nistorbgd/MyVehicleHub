package com.nst.myvehiclehub.controller;

import com.nst.myvehiclehub.dto.response.GetProfileResponse;
import com.nst.myvehiclehub.entity.UserPrincipal;
import com.nst.myvehiclehub.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

  private final UserService userServiceImpl;

  public UserController(UserService userServiceImpl) {
    this.userServiceImpl = userServiceImpl;
  }

  @GetMapping("/profile")
  public GetProfileResponse getCurrentUserProfile(Authentication authentication) {
    UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
    return userServiceImpl.getProfile(principal.getUser().getId());
  }
}
