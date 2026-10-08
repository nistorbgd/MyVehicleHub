package com.nst.myvehiclehub.service;

import com.nst.myvehiclehub.dto.HistoryEntryDTO;
import com.nst.myvehiclehub.entity.AppUserRecord;
import java.util.List;

public interface HistoryEntryService {
  HistoryEntryDTO createHistoryEntry(HistoryEntryDTO request, Long vehicleId, AppUserRecord user);

  List<HistoryEntryDTO> getHistoryEntries(Long vehicleId, AppUserRecord user);
}
