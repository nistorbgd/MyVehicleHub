package com.nst.myvehiclehub.serviceImpl;

import com.nst.myvehiclehub.dto.HistoryEntryDTO;
import com.nst.myvehiclehub.entity.AppUserRecord;
import com.nst.myvehiclehub.mapper.HistoryEntryMapper;
import com.nst.myvehiclehub.repository.HistoryEntryRepository;
import com.nst.myvehiclehub.repository.VehicleRepository;
import com.nst.myvehiclehub.service.HistoryEntryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class HistoryEntryServiceImpl implements HistoryEntryService {

  private final VehicleRepository vehicleRepository;
  private final HistoryEntryRepository historyEntryRepository;
  private final HistoryEntryMapper historyEntryMapper;

  @Override
  public HistoryEntryDTO createHistoryEntry(
      HistoryEntryDTO request, Long vehicleId, AppUserRecord user) {
    var vehicle =
        vehicleRepository
            .findByIdAndUser(vehicleId, user)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found"));

    var newHistoryEntry = historyEntryMapper.mapRequestDTOtoRecord(request);
    newHistoryEntry.setVehicle(vehicle);

    var savedHistoryEntry = historyEntryRepository.save(newHistoryEntry);

    return historyEntryMapper.mapRecordToResponseDTO(savedHistoryEntry);
  }

  @Override
  public List<HistoryEntryDTO> getHistoryEntries(Long vehicleId, AppUserRecord user) {
    var vehicle =
        vehicleRepository
            .findByIdAndUser(vehicleId, user)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found"));
    var historyEntries =
        historyEntryRepository
            .findByVehicle(vehicle)
            .orElseThrow(
                () ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "History entries not found"));

    return historyEntries.stream().map(historyEntryMapper::mapRecordToResponseDTO).toList();
  }
}
