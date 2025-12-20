package com.nst.myvehiclehub.service;

import com.nst.myvehiclehub.entity.AppUser;
import com.nst.myvehiclehub.repository.AppUserRepository;
import com.nst.myvehiclehub.request.LoginRequest;
import com.nst.myvehiclehub.request.RegisterRequest;
import com.nst.myvehiclehub.response.LoginResponse;
import com.nst.myvehiclehub.response.RegisterResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final AuthenticationManager authManager;
    private final JWTService jwtService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(AppUserRepository appUserRepository, AuthenticationManager authenticationManager, JWTService jwtService) {
        this.appUserRepository = appUserRepository;
        this.authManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public RegisterResponse register(RegisterRequest registerRequest) {
        validateRegisterRequest(registerRequest);
        AppUser newUser = AppUser.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .lastName(registerRequest.getLastName())
                .firstName(registerRequest.getFirstName())
                .age(registerRequest.getAge())
                .build();

        appUserRepository.save(newUser);
        return new RegisterResponse(newUser.getId().toString());
    }

    private void validateRegisterRequest(RegisterRequest registerRequest) {
        if (registerRequest.getEmail() == null || registerRequest.getPassword() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email or password is missing");
        }

        if (appUserRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use");
        }
    }


    public LoginResponse login(LoginRequest loginRequest) {
        validateLoginRequest(loginRequest);
        Authentication authentication =
                authManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        if (authentication.isAuthenticated()) {
            return new LoginResponse(jwtService.generateToken(loginRequest.getEmail()));
        }

        return new LoginResponse("User or password is incorrect");
    }

    private void validateLoginRequest(LoginRequest loginRequest) {
        if (loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email or password is missing");
        }
    }
}
