package com.wms.warehouse.controller;

import com.wms.common.dto.PageResponse;
import com.wms.common.enums.WarehouseStatus;
import com.wms.common.security.UserContextHolder;
import com.wms.warehouse.dto.excelimport.WarehouseExcelImportResponse;
import com.wms.warehouse.dto.warehouse.*;
import com.wms.warehouse.dto.warehouse.AddWarehouseImageRequest;
import com.wms.warehouse.dto.warehouse.CreateWarehouseRequest;
import com.wms.warehouse.dto.warehouse.UpdateWarehouseRequest;
import com.wms.warehouse.service.WarehouseExcelService;
import com.wms.warehouse.service.WarehouseImageService;
import com.wms.warehouse.service.WarehouseService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/owner/warehouses")
@RequiredArgsConstructor
public class OwnerWarehouseController {

  private static final Set<String> ALLOWED_SORT_FIELDS =
      Set.of("createdAt", "updatedAt", "name", "city", "totalSurfaceArea");

  private final WarehouseService warehouseService;
  private final WarehouseImageService warehouseImageService;
  private final WarehouseExcelService warehouseExcelService;

  @PostMapping
  public ResponseEntity<com.wms.warehouse.dto.warehouse.WarehouseResponse> create(
      @Valid @RequestBody CreateWarehouseRequest request) {
    UUID ownerId = UserContextHolder.get().userId();
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(warehouseService.create(ownerId, request));
  }

  @GetMapping
  public ResponseEntity<PageResponse<com.wms.warehouse.dto.warehouse.WarehouseResponse>> listOwn(
      @RequestParam(required = false) String search,
      @RequestParam(required = false) WarehouseStatus status,
      @RequestParam(required = false) Instant createdFrom,
      @RequestParam(required = false) Instant createdTo,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String sortDir) {

    UUID ownerId = UserContextHolder.get().userId();
    String safeSortBy = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "createdAt";
    Sort sort =
        sortDir.equalsIgnoreCase("asc")
            ? Sort.by(safeSortBy).ascending()
            : Sort.by(safeSortBy).descending();

    return ResponseEntity.ok(
        warehouseService.searchOwn(
            ownerId,
            search,
            status,
            createdFrom,
            createdTo,
            PageRequest.of(page, Math.min(size, 100), sort)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<com.wms.warehouse.dto.warehouse.WarehouseResponse> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(warehouseService.getById(id));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<com.wms.warehouse.dto.warehouse.WarehouseResponse> update(
      @PathVariable UUID id, @Valid @RequestBody UpdateWarehouseRequest request) {
    UUID ownerId = UserContextHolder.get().userId();
    return ResponseEntity.ok(warehouseService.update(id, ownerId, request));
  }

  @PatchMapping("/{id}/publish")
  public ResponseEntity<com.wms.warehouse.dto.warehouse.WarehouseResponse> publish(@PathVariable UUID id) {
    UUID ownerId = UserContextHolder.get().userId();
    return ResponseEntity.ok(warehouseService.publish(id, ownerId));
  }

  @PostMapping("/{id}/images")
  public ResponseEntity<com.wms.warehouse.dto.warehouse.WarehouseImageResponse> addImage(
      @PathVariable UUID id, @Valid @RequestBody AddWarehouseImageRequest request) {
    UUID ownerId = UserContextHolder.get().userId();
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(warehouseImageService.addImage(id, ownerId, request));
  }

  @DeleteMapping("/{id}/images/{imageId}")
  public ResponseEntity<Void> deleteImage(@PathVariable UUID id, @PathVariable UUID imageId) {
    UUID ownerId = UserContextHolder.get().userId();
    warehouseImageService.deleteImage(id, imageId, ownerId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}/import/template")
  public ResponseEntity<byte[]> downloadImportTemplate(@PathVariable UUID id) {
    byte[] template = warehouseExcelService.generateTemplate();
    return ResponseEntity.ok()
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=warehouse-blueprint-template.xlsx")
        .contentType(
            MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        .body(template);
  }

  @PostMapping("/{id}/import")
  public ResponseEntity<WarehouseExcelImportResponse> uploadImport(
      @PathVariable UUID id, @RequestParam("file") MultipartFile file) throws IOException {
    UUID ownerId = UserContextHolder.get().userId();
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            warehouseExcelService.processImport(
                id, ownerId, file.getOriginalFilename(), file.getInputStream()));
  }
}
