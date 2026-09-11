package com.nst.myvehiclehub.service;

import com.nst.myvehiclehub.dto.request.VehicleRequestDTO;
import com.nst.myvehiclehub.dto.response.VehicleBlotterDTO;
import com.nst.myvehiclehub.dto.response.VehicleDTO;
import com.nst.myvehiclehub.entity.AppUserRecord;

public interface VehicleService {
  VehicleDTO addVehicle(VehicleRequestDTO request, AppUserRecord user);

  VehicleBlotterDTO getVehicles(AppUserRecord user);
}
