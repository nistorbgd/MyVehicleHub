package com.nst.myvehiclehub.service;

import com.nst.myvehiclehub.entity.AppUser;
import com.nst.myvehiclehub.repository.AppUserRepository;
import com.nst.myvehiclehub.response.GetProfileResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class UserService {

    private final AppUserRepository userRepository;

    public UserService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public GetProfileResponse getProfile(UUID userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return new GetProfileResponse(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getAge()
        );
    }
}
