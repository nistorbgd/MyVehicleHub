package com.nst.myvehiclehub.controller;

import com.nst.myvehiclehub.dto.response.GetProfileResponse;
import com.nst.myvehiclehub.entity.UserPrincipal;
import com.nst.myvehiclehub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping(
      path = "/profile",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<GetProfileResponse> getCurrentUserProfile(Authentication authentication) {
    UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
    return ResponseEntity.ok(userService.getProfile(principal.getUser().getId()));
  }
}
