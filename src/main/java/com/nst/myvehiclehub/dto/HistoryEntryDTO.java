package com.nst.myvehiclehub.dto;

import com.nst.myvehiclehub.enums.HistoryEntryType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryEntryDTO {

  private Long id;
  private String title;
  private String description;
  private Double cost;
  private LocalDateTime serviceTime;
  private HistoryEntryType type;
}
