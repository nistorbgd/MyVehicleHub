package com.nst.myvehiclehub.dto.response;

import com.nst.myvehiclehub.dto.VehicleDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleBlotterDTO {
  private List<VehicleDTO> vehicles;
}
