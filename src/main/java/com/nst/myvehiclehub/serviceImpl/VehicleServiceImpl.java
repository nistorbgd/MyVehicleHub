package com.nst.myvehiclehub.serviceImpl;

import com.nst.myvehiclehub.dto.request.VehicleRequestDTO;
import com.nst.myvehiclehub.dto.response.VehicleBlotterDTO;
import com.nst.myvehiclehub.dto.response.VehicleDTO;
import com.nst.myvehiclehub.entity.AppUser;
import com.nst.myvehiclehub.entity.Vehicle;
import com.nst.myvehiclehub.repository.VehicleRepository;
import com.nst.myvehiclehub.service.VehicleService;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class VehicleServiceImpl implements VehicleService {

  private final VehicleRepository vehicleRepository;

  public VehicleServiceImpl(VehicleRepository vehicleRepository) {
    this.vehicleRepository = vehicleRepository;
  }

  public VehicleDTO addVehicle(VehicleRequestDTO request, AppUser user) {
    validateAddVehicleRequest(request);

    Vehicle vehicle =
        Vehicle.builder()
            .make(request.getMake())
            .model(request.getModel())
            .year(request.getYear())
            .plateNumber(request.getPlateNumber())
            .vin(request.getVin())
            .user(user)
            .build();

    Vehicle savedVehicle = vehicleRepository.save(vehicle);

    return new VehicleDTO(
        savedVehicle.getId(),
        savedVehicle.getMake(),
        savedVehicle.getModel(),
        savedVehicle.getYear(),
        savedVehicle.getPlateNumber(),
        savedVehicle.getVin(),
        "Vehicle added successfully");
  }

  private void validateAddVehicleRequest(VehicleRequestDTO request) {
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

  public VehicleBlotterDTO getVehicles(AppUser user) {
    List<Vehicle> vehicleList = vehicleRepository.findByUser(user);

    List<VehicleDTO> vehicles =
        vehicleList.stream()
            .map(
                vehicle ->
                    new VehicleDTO(
                        vehicle.getId(),
                        vehicle.getMake(),
                        vehicle.getModel(),
                        vehicle.getYear(),
                        vehicle.getPlateNumber(),
                        vehicle.getVin(),
                        null // No message needed for list items
                        ))
            .collect(Collectors.toList());

    return new VehicleBlotterDTO(vehicles);
  }
}
