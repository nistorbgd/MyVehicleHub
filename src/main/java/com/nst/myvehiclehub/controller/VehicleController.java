package com.nst.myvehiclehub.controller;

import com.nst.myvehiclehub.dto.request.AddVehicleRequest;
import com.nst.myvehiclehub.dto.response.AllVehiclesResponse;
import com.nst.myvehiclehub.dto.response.VehicleResponse;
import com.nst.myvehiclehub.entity.UserPrincipal;
import com.nst.myvehiclehub.service.VehicleService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController {
  private final VehicleService vehicleServiceImpl;

  public VehicleController(VehicleService vehicleServiceImpl) {
    this.vehicleServiceImpl = vehicleServiceImpl;
  }

  @PostMapping
  public VehicleResponse addVehicle(
      @RequestBody AddVehicleRequest request, Authentication authentication) {
    UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
    return vehicleServiceImpl.addVehicle(request, principal.getUser());
  }

  @GetMapping
  public AllVehiclesResponse getVehicles(Authentication authentication) {
    UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
    return vehicleServiceImpl.getVehicles(principal.getUser());
  }
}
