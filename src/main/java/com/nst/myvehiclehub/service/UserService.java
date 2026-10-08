package com.nst.myvehiclehub.service;

import com.nst.myvehiclehub.dto.response.ProfileDTO;
import java.util.UUID;

public interface UserService {
  ProfileDTO getProfile(UUID userId);
}
