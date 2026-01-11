package com.nst.myvehiclehub.service;

import com.nst.myvehiclehub.entity.AppUser;
import com.nst.myvehiclehub.entity.Vehicle;
import com.nst.myvehiclehub.repository.VehicleRepository;
import com.nst.myvehiclehub.request.AddVehicleRequest;
import com.nst.myvehiclehub.response.AllVehiclesResponse;
import com.nst.myvehiclehub.response.VehicleResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public VehicleResponse addVehicle(AddVehicleRequest request, AppUser user) {
        validateAddVehicleRequest(request);

        Vehicle vehicle = Vehicle.builder()
                .make(request.getMake())
                .model(request.getModel())
                .year(request.getYear())
                .plateNumber(request.getPlateNumber())
                .vin(request.getVin())
                .user(user)
                .build();

        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        return new VehicleResponse(
                savedVehicle.getId(),
                savedVehicle.getMake(),
                savedVehicle.getModel(),
                savedVehicle.getYear(),
                savedVehicle.getPlateNumber(),
                savedVehicle.getVin(),
                "Vehicle added successfully"
        );
    }

    private void validateAddVehicleRequest(AddVehicleRequest request) {
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

    public AllVehiclesResponse getVehicles(AppUser user) {
        List<Vehicle> vehicleList = vehicleRepository.findByUser(user);

        List<VehicleResponse> vehicleResponses = vehicleList.stream()
                .map(vehicle -> new VehicleResponse(
                        vehicle.getId(),
                        vehicle.getMake(),
                        vehicle.getModel(),
                        vehicle.getYear(),
                        vehicle.getPlateNumber(),
                        vehicle.getVin(),
                        null  // No message needed for list items
                ))
                .collect(Collectors.toList());

        return new AllVehiclesResponse(vehicleResponses);
    }
}
