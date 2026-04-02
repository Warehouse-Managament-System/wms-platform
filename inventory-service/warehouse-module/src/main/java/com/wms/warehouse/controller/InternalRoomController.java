package com.wms.warehouse.controller;

import com.wms.common.dto.RoomAvailabilityResponse;
import com.wms.warehouse.service.RoomAvailabilityService;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/internal/rooms")
@RequiredArgsConstructor
public class InternalRoomController {

  private final RoomAvailabilityService roomAvailabilityService;

  @GetMapping("/{id}/availability")
  public RoomAvailabilityResponse checkAvailability(
      @PathVariable UUID id,
      @RequestParam LocalDate startDate,
      @RequestParam LocalDate endDate) {
    return roomAvailabilityService.checkAvailability(id, startDate, endDate);
  }
}
