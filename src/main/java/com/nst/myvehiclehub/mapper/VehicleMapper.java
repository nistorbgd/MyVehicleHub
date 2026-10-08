package com.nst.myvehiclehub.mapper;

import static java.util.Objects.isNull;

import com.nst.myvehiclehub.dto.VehicleDTO;
import com.nst.myvehiclehub.entity.VehicleRecord;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class VehicleMapper {

  private final HistoryEntryMapper historyEntryMapper;

  public VehicleRecord mapRequestDTOtoRecord(VehicleDTO request) {
    if (isNull(request)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vehicle object can not be null");
    }

    return VehicleRecord.builder()
        .model(request.getModel())
        .make(request.getMake())
        .vin(request.getVin())
        .plateNumber(request.getPlateNumber())
        .year(request.getYear())
        .historyEntries(new ArrayList<>())
        .build();
  }

  public VehicleDTO mapRecordToResponseDTO(VehicleRecord record) {

    var historyEntries =
        record.getHistoryEntries().stream()
            .map(historyEntryMapper::mapRecordToResponseDTO)
            .toList();

    return VehicleDTO.builder()
        .id(record.getId())
        .model(record.getModel())
        .make(record.getMake())
        .vin(record.getVin())
        .plateNumber(record.getPlateNumber())
        .year(record.getYear())
        .historyEntries(historyEntries)
        .build();
  }
}
