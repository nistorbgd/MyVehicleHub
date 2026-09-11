package com.nst.myvehiclehub.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllVehiclesResponse {
  private List<VehicleResponse> vehicles;
}
