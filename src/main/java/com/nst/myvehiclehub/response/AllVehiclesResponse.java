package com.nst.myvehiclehub.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllVehiclesResponse {
    private List<VehicleResponse> vehicles;
}
