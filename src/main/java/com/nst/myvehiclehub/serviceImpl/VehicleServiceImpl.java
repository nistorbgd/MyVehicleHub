package com.nst.myvehiclehub.serviceImpl;

import com.nst.myvehiclehub.dto.VehicleDTO;
import com.nst.myvehiclehub.dto.response.VehicleBlotterDTO;
import com.nst.myvehiclehub.entity.AppUserRecord;
import com.nst.myvehiclehub.mapper.VehicleMapper;
import com.nst.myvehiclehub.repository.VehicleRepository;
import com.nst.myvehiclehub.service.VehicleService;
import java.time.Year;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

  private final VehicleRepository vehicleRepository;

  private final VehicleMapper vehicleMapper;

  public VehicleDTO createVehicle(VehicleDTO request, AppUserRecord user) {
    validateVehicleDetails(request);

    var newVehicleRecord = vehicleMapper.mapRequestDTOtoRecord(request);
    newVehicleRecord.setUser(user);

    var savedVehicleRecord = vehicleRepository.save(newVehicleRecord);

    return vehicleMapper.mapRecordToResponseDTO(savedVehicleRecord);
  }

  private void validateVehicleDetails(VehicleDTO request) {
    if (request.getMake() == null || request.getMake().trim().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Make is required");
    }
    if (request.getModel() == null || request.getModel().trim().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Model is required");
    }
    if (request.getYear() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Year is required");
    }
    int currentYear = Year.now().getValue();
    if (request.getYear() < 1900 || request.getYear() > currentYear) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid year");
    }
  }

  public VehicleBlotterDTO getVehicles(AppUserRecord user) {
    var vehicleRecordList = vehicleRepository.findByUser(user);

    var vehicles = vehicleRecordList.stream().map(vehicleMapper::mapRecordToResponseDTO).toList();

    return new VehicleBlotterDTO(vehicles);
  }
}
