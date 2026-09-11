package com.nst.myvehiclehub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponse {
  private Long id;
  private String make;
  private String model;
  private Integer year;
  private String plateNumber;
  private String vin;
  private String message;
}
