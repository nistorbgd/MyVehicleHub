package com.nst.myvehiclehub.controller;


import com.nst.myvehiclehub.entity.UserPrincipal;
import com.nst.myvehiclehub.response.GetProfileResponse;
import com.nst.myvehiclehub.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public GetProfileResponse getCurrentUserProfile(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return userService.getProfile(principal.getUser().getId());
    }
}
