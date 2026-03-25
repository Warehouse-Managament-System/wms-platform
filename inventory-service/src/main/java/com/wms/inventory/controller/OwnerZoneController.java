package com.wms.inventory.controller;

import com.wms.common.security.UserContextHolder;
import com.wms.inventory.dto.zone.CreateZoneRequest;
import com.wms.inventory.dto.zone.UpdateZoneRequest;
import com.wms.inventory.dto.zone.ZoneAvailabilityRequest;
import com.wms.inventory.dto.zone.ZoneAvailabilityResponse;
import com.wms.inventory.dto.zone.ZoneResponse;
import com.wms.inventory.service.ZoneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/owner")
@RequiredArgsConstructor
public class OwnerZoneController {
    private final ZoneService zoneService;

    // POST /api/v1/owner/warehouses/{warehouseId}/zones
    @PostMapping("/warehouses/{warehouseId}/zones")
    public ResponseEntity<ZoneResponse> create(
        @PathVariable UUID warehouseId,
        @Valid @RequestBody CreateZoneRequest request
    ) {
        UUID ownerId = UserContextHolder.get().userId();
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(zoneService.create(warehouseId, ownerId, request));
    }

    // PATCH /api/v1/owner/zones/{id}
    @PatchMapping("/zones/{id}")
    public ResponseEntity<ZoneResponse> update(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateZoneRequest request
    ) {
        UUID ownerId = UserContextHolder.get().userId();
        return ResponseEntity.ok(zoneService.update(id, ownerId, request));
    }

    // POST /api/v1/owner/zones/{id}/availabilities
    @PostMapping("/zones/{id}/availabilities")
    public ResponseEntity<ZoneAvailabilityResponse> addAvailability(
        @PathVariable UUID id,
        @Valid @RequestBody ZoneAvailabilityRequest request
    ) {
        UUID ownerId = UserContextHolder.get().userId();
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(zoneService.addAvailability(id, ownerId, request));
    }

    // POST /api/v1/owner/zones/{id}/categories
    @PostMapping("/zones/{id}/categories")
    public ResponseEntity<Void> addCategory(
        @PathVariable UUID id,
        @RequestBody Map<String, UUID> body
    ) {
        UUID ownerId = UserContextHolder.get().userId();
        UUID categoryId = body.get("categoryId");
        zoneService.addCategory(id, ownerId, categoryId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
