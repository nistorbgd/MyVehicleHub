package com.nst.myvehiclehub.controller;

import com.nst.myvehiclehub.entity.UserPrincipal;
import com.nst.myvehiclehub.request.AddVehicleRequest;
import com.nst.myvehiclehub.response.AllVehiclesResponse;
import com.nst.myvehiclehub.response.VehicleResponse;
import com.nst.myvehiclehub.service.VehicleService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController {
  private final VehicleService vehicleService;

  public VehicleController(VehicleService vehicleService) {
    this.vehicleService = vehicleService;
  }

  @PostMapping
  public VehicleResponse addVehicle(
      @RequestBody AddVehicleRequest request, Authentication authentication) {
    UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
    return vehicleService.addVehicle(request, principal.getUser());
  }

  @GetMapping
  public AllVehiclesResponse getVehicles(Authentication authentication) {
    UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
    return vehicleService.getVehicles(principal.getUser());
  }
}
