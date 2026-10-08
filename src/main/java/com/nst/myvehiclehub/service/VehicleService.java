package com.nst.myvehiclehub.service;

import com.nst.myvehiclehub.dto.VehicleDTO;
import com.nst.myvehiclehub.dto.response.VehicleBlotterDTO;
import com.nst.myvehiclehub.entity.AppUserRecord;

public interface VehicleService {
  VehicleDTO createVehicle(VehicleDTO request, AppUserRecord user);

  VehicleBlotterDTO getVehicles(AppUserRecord user);
}
