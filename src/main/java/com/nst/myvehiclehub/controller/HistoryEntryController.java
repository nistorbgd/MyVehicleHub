package com.nst.myvehiclehub.controller;

import com.nst.myvehiclehub.dto.HistoryEntryDTO;
import com.nst.myvehiclehub.entity.UserPrincipalRecord;
import com.nst.myvehiclehub.service.HistoryEntryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/history")
@RequiredArgsConstructor
public class HistoryEntryController {

  private final HistoryEntryService historyEntryService;

  @PostMapping(
      path = "/vehicles/{vehicleId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<HistoryEntryDTO> createHistoryEntry(
      @PathVariable final Long vehicleId,
      @RequestBody HistoryEntryDTO request,
      Authentication authentication) {
    var principal = (UserPrincipalRecord) authentication.getPrincipal();
    return ResponseEntity.ok(
        historyEntryService.createHistoryEntry(request, vehicleId, principal.getUser()));
  }

  @GetMapping(path = "/vehicles/{vehicleId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<HistoryEntryDTO>> getHistoryEntries(
      @PathVariable final Long vehicleId, Authentication authentication) {
    var principal = (UserPrincipalRecord) authentication.getPrincipal();
    return ResponseEntity.ok(historyEntryService.getHistoryEntries(vehicleId, principal.getUser()));
  }
}
