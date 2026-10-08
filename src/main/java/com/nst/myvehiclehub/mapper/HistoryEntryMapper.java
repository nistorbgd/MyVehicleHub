package com.nst.myvehiclehub.mapper;

import static java.util.Objects.isNull;

import com.nst.myvehiclehub.dto.HistoryEntryDTO;
import com.nst.myvehiclehub.entity.HistoryEntryRecord;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class HistoryEntryMapper {

  public HistoryEntryRecord mapRequestDTOtoRecord(HistoryEntryDTO request) {
    if (isNull(request)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The historyEntry can not be null");
    }

    return HistoryEntryRecord.builder()
        .title(request.getTitle())
        .description(request.getDescription())
        .cost(request.getCost())
        .serviceTime(request.getServiceTime())
        .type(request.getType())
        .build();
  }

  public HistoryEntryDTO mapRecordToResponseDTO(HistoryEntryRecord record) {

    return HistoryEntryDTO.builder()
        .id(record.getId())
        .title(record.getTitle())
        .description(record.getDescription())
        .cost(record.getCost())
        .serviceTime(record.getServiceTime())
        .type(record.getType())
        .build();
  }
}
