package com.nst.myvehiclehub.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {
  private Long id;
  private String make;
  private String model;
  private Integer year;
  private String plateNumber;
  private String vin;
  private List<HistoryEntryDTO> historyEntries;
}
