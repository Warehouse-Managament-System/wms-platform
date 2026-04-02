package com.wms.warehouse.controller;

import com.wms.common.security.UserContextHolder;
import com.wms.warehouse.dto.zone.AddZoneCategoryRequest;
import com.wms.warehouse.dto.zone.CreateZoneRequest;
import com.wms.warehouse.dto.zone.UpdateZoneRequest;
import com.wms.warehouse.dto.zone.ZoneAvailabilityRequest;
import com.wms.warehouse.dto.zone.ZoneAvailabilityResponse;
import com.wms.warehouse.dto.zone.ZoneResponse;
import com.wms.warehouse.service.ZoneService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/owner")
@RequiredArgsConstructor
public class OwnerZoneController {
  private final ZoneService zoneService;

  @PostMapping("/warehouses/{warehouseId}/zones")
  public ResponseEntity<ZoneResponse> create(
      @PathVariable UUID warehouseId, @Valid @RequestBody CreateZoneRequest request) {
    UUID ownerId = UserContextHolder.get().userId();
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(zoneService.create(warehouseId, ownerId, request));
  }

  @PatchMapping("/zones/{id}")
  public ResponseEntity<ZoneResponse> update(
      @PathVariable UUID id, @Valid @RequestBody UpdateZoneRequest request) {
    UUID ownerId = UserContextHolder.get().userId();
    return ResponseEntity.ok(zoneService.update(id, ownerId, request));
  }

  @PostMapping("/zones/{id}/availabilities")
  public ResponseEntity<ZoneAvailabilityResponse> addAvailability(
      @PathVariable UUID id, @Valid @RequestBody ZoneAvailabilityRequest request) {
    UUID ownerId = UserContextHolder.get().userId();
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(zoneService.addAvailability(id, ownerId, request));
  }

  @DeleteMapping("/zones/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    UUID ownerId = UserContextHolder.get().userId();
    zoneService.delete(id, ownerId);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/zones/{id}/categories")
  public ResponseEntity<Void> addCategory(
      @PathVariable UUID id, @Valid @RequestBody AddZoneCategoryRequest request) {
    UUID ownerId = UserContextHolder.get().userId();
    zoneService.addCategory(id, ownerId, request.categoryId());
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}
