package com.wms.warehouse.controller;

import com.wms.common.dto.PageResponse;
import com.wms.warehouse.dto.warehouse.WarehouseResponse;
import com.wms.warehouse.service.WarehouseService;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/warehouses")
@RequiredArgsConstructor
public class PublicWarehouseController {

  private static final Set<String> ALLOWED_SORT_FIELDS =
      Set.of("createdAt", "name", "city", "totalSurfaceArea");

  private final WarehouseService warehouseService;

  @GetMapping
  public ResponseEntity<PageResponse<WarehouseResponse>> browsePublished(
      @RequestParam(required = false) String search,
      @RequestParam(required = false) String city,
      @RequestParam(required = false) String country,
      @RequestParam(required = false) Instant createdFrom,
      @RequestParam(required = false) Instant createdTo,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String sortDir) {

    String safeSortBy = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "createdAt";
    Sort sort =
        sortDir.equalsIgnoreCase("asc")
            ? Sort.by(safeSortBy).ascending()
            : Sort.by(safeSortBy).descending();

    return ResponseEntity.ok(
        warehouseService.searchPublished(
            search,
            city,
            country,
            createdFrom,
            createdTo,
            PageRequest.of(page, Math.min(size, 100), sort)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<WarehouseResponse> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(warehouseService.getById(id));
  }
}
