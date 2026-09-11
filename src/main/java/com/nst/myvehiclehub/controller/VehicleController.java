package com.nst.myvehiclehub.controller;

import com.nst.myvehiclehub.dto.request.VehicleRequestDTO;
import com.nst.myvehiclehub.dto.response.VehicleBlotterDTO;
import com.nst.myvehiclehub.dto.response.VehicleDTO;
import com.nst.myvehiclehub.entity.UserPrincipal;
import com.nst.myvehiclehub.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class VehicleController {
  private final VehicleService vehicleService;

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<VehicleDTO> addVehicle(
      @RequestBody VehicleRequestDTO request, Authentication authentication) {
    UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
    return ResponseEntity.ok(vehicleService.addVehicle(request, principal.getUser()));
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<VehicleBlotterDTO> getVehicles(Authentication authentication) {
    UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
    return ResponseEntity.ok(vehicleService.getVehicles(principal.getUser()));
  }
}
