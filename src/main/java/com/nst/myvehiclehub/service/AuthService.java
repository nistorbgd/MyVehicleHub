package com.nst.myvehiclehub.service;

import com.nst.myvehiclehub.entity.AppUser;
import com.nst.myvehiclehub.repository.AppUserRepository;
import com.nst.myvehiclehub.request.LoginRequest;
import com.nst.myvehiclehub.request.RegisterRequest;
import com.nst.myvehiclehub.response.LoginResponse;
import com.nst.myvehiclehub.response.RegisterResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;

    public AuthService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public RegisterResponse createUser(RegisterRequest registerRequest) {
        validateRegisterRequest(registerRequest);
        AppUser newUser = AppUser.builder()
                .email(registerRequest.getEmail())
                .password(registerRequest.getPassword())
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
        if (appUserRepository.existsByEmail(loginRequest.getEmail())) {
            AppUser user = appUserRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            if (user.getPassword().equals(loginRequest.getPassword())) {
                return new LoginResponse("DONE");
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid password");
            }
        }

        return new LoginResponse("User or password is incorrect");
    }

    private void validateLoginRequest(LoginRequest loginRequest) {
        if (loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email or password is missing");
        }
    }
}
