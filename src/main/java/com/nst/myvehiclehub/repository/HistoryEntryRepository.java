package com.nst.myvehiclehub.repository;

import com.nst.myvehiclehub.entity.HistoryEntryRecord;
import com.nst.myvehiclehub.entity.VehicleRecord;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryEntryRepository extends JpaRepository<HistoryEntryRecord, Long> {

  Optional<List<HistoryEntryRecord>> findByVehicle(VehicleRecord vehicle);
}
