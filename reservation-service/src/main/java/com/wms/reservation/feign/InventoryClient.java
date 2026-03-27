package com.wms.reservation.feign;

import com.wms.common.dto.RoomAvailabilityResponse;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "inventory")
public interface InventoryClient {

  @GetMapping("/api/v1/internal/rooms/{id}/availability")
  RoomAvailabilityResponse checkRoomAvailability(
      @PathVariable UUID id, @RequestParam LocalDate startDate, @RequestParam LocalDate endDate);
}
