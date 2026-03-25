package com.wms.inventory.controller;

import com.wms.common.dto.PageResponse;
import com.wms.common.enums.TemperatureType;
import com.wms.common.enums.ZoneStatus;
import com.wms.inventory.dto.zone.ZoneResponse;
import com.wms.inventory.service.ZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;


import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/warehouses/{warehouseId}/zones")
@RequiredArgsConstructor
public class PublicZoneController {
    private final ZoneService zoneService;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("createdAt", "name");

    @GetMapping
    public ResponseEntity<PageResponse<ZoneResponse>> list(
        @PathVariable UUID warehouseId,
        @RequestParam(required = false) TemperatureType temperatureType,
        @RequestParam(required = false) ZoneStatus status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "createdAt") String sortBy,
        @RequestParam(defaultValue = "desc") String sortDir
    ) {
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            sortBy = "createdAt";
        }

        Sort sort = sortDir.equalsIgnoreCase("asc")
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();

        PageRequest pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(zoneService.listByWarehouse(warehouseId, temperatureType, status, pageable));
    }
}
