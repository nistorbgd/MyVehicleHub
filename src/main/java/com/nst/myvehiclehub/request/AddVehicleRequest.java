package com.nst.myvehiclehub.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddVehicleRequest {
  private String make;
  private String model;
  private Integer year;
  private String plateNumber;
  private String vin;
}
