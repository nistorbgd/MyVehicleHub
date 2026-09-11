package com.nst.myvehiclehub.serviceImpl;

import com.nst.myvehiclehub.dto.request.VehicleRequestDTO;
import com.nst.myvehiclehub.dto.response.VehicleBlotterDTO;
import com.nst.myvehiclehub.dto.response.VehicleDTO;
import com.nst.myvehiclehub.entity.AppUserRecord;
import com.nst.myvehiclehub.entity.VehicleRecord;
import com.nst.myvehiclehub.repository.VehicleRepository;
import com.nst.myvehiclehub.service.VehicleService;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

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

  public VehicleDTO createVehicle(VehicleRequestDTO request, AppUserRecord user) {
    validateVehicleDetails(request);

    var vehicleRecord =
        VehicleRecord.builder()
            .make(request.getMake())
            .model(request.getModel())
            .year(request.getYear())
            .plateNumber(request.getPlateNumber())
            .vin(request.getVin())
            .user(user)
            .build();

    var savedVehicleRecord = vehicleRepository.save(vehicleRecord);

    return new VehicleDTO(
        savedVehicleRecord.getId(),
        savedVehicleRecord.getMake(),
        savedVehicleRecord.getModel(),
        savedVehicleRecord.getYear(),
        savedVehicleRecord.getPlateNumber(),
        savedVehicleRecord.getVin(),
        "Vehicle added successfully");
  }

  private void validateVehicleDetails(VehicleRequestDTO request) {
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

    var vehicles =
        vehicleRecordList.stream()
            .map(
                vehicleRecord ->
                    new VehicleDTO(
                        vehicleRecord.getId(),
                        vehicleRecord.getMake(),
                        vehicleRecord.getModel(),
                        vehicleRecord.getYear(),
                        vehicleRecord.getPlateNumber(),
                        vehicleRecord.getVin(),
                        null // No message needed for list items
                        ))
            .collect(Collectors.toList());

    return new VehicleBlotterDTO(vehicles);
  }
}
