package com.nst.myvehiclehub.service;

import com.nst.myvehiclehub.dto.request.VehicleRequestDTO;
import com.nst.myvehiclehub.dto.response.VehicleBlotterDTO;
import com.nst.myvehiclehub.dto.response.VehicleDTO;
import com.nst.myvehiclehub.entity.AppUser;

public interface VehicleService {
  VehicleDTO addVehicle(VehicleRequestDTO request, AppUser user);

  VehicleBlotterDTO getVehicles(AppUser user);
}
