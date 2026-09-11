package com.nst.myvehiclehub.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRequestDTO {
  private String make;
  private String model;
  private Integer year;
  private String plateNumber;
  private String vin;
}
