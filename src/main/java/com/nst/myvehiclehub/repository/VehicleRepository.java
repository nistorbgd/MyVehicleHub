package com.nst.myvehiclehub.repository;

import com.nst.myvehiclehub.entity.AppUserRecord;
import com.nst.myvehiclehub.entity.VehicleRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<VehicleRecord, Long> {
  List<VehicleRecord> findByUser(AppUserRecord user);

  List<VehicleRecord> findByVin(String vin);

  AppUserRecord user(AppUserRecord user);
}
