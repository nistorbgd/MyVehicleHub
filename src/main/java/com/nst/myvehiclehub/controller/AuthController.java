package com.nst.myvehiclehub.controller;

import com.nst.myvehiclehub.request.LoginRequest;
import com.nst.myvehiclehub.request.RegisterRequest;
import com.nst.myvehiclehub.response.LoginResponse;
import com.nst.myvehiclehub.response.RegisterResponse;
import com.nst.myvehiclehub.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(@RequestBody RegisterRequest registerRequest) {
        return authService.createUser(registerRequest);
    }

    @PostMapping("/login")
    public LoginResponse login (@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }
}
