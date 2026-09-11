package com.nst.myvehiclehub.service;

import com.nst.myvehiclehub.dto.request.AddVehicleRequest;
import com.nst.myvehiclehub.dto.response.AllVehiclesResponse;
import com.nst.myvehiclehub.entity.AppUser;

public interface VehicleService {
  com.nst.myvehiclehub.dto.response.VehicleResponse addVehicle(
      AddVehicleRequest request, AppUser user);

  AllVehiclesResponse getVehicles(AppUser user);
}
