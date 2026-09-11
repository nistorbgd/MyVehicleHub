package com.nst.myvehiclehub.repository;

import com.nst.myvehiclehub.entity.AppUser;
import com.nst.myvehiclehub.entity.Vehicle;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
  List<Vehicle> findByUser(AppUser user);

  List<Vehicle> findByVin(String vin);

  AppUser user(AppUser user);
}
