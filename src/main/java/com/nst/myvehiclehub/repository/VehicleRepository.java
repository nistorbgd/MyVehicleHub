package com.nst.myvehiclehub.repository;

import com.nst.myvehiclehub.entity.AppUser;
import com.nst.myvehiclehub.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByUser(AppUser user);

    List<Vehicle> findByVin(String vin);

    AppUser user(AppUser user);
}
