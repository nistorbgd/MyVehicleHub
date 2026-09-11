package com.nst.myvehiclehub.service;

import com.nst.myvehiclehub.dto.response.GetProfileResponse;
import java.util.UUID;

public interface UserService {
  GetProfileResponse getProfile(UUID userId);
}
