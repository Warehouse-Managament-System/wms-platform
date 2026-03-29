package com.wms.warehouse.controller;

import com.wms.common.dto.PageResponse;
import com.wms.common.enums.RoomStatus;
import com.wms.warehouse.dto.room.RoomResponse;
import com.wms.warehouse.service.RoomService;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/zones/{zoneId}/rooms")
@RequiredArgsConstructor
public class PublicRoomController {

  private final RoomService roomService;

  private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("createdAt", "name");

  @GetMapping
  public ResponseEntity<PageResponse<RoomResponse>> list(
      @PathVariable UUID zoneId,
      @RequestParam(required = false) RoomStatus status,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String sortDir) {
    if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
      sortBy = "createdAt";
    }

    Sort sort =
        sortDir.equalsIgnoreCase("asc")
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();

    PageRequest pageable = PageRequest.of(page, Math.min(size, 100), sort);
    return ResponseEntity.ok(roomService.listByZone(zoneId, status, pageable));
  }
}
