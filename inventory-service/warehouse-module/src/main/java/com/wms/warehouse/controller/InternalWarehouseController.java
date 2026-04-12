package com.wms.warehouse.controller;

import com.wms.common.exception.EntityNotFoundException;
import com.wms.warehouse.repository.WarehouseRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Internal endpoints used by other services to verify warehouse existence. Not intended for client
 * use; should be reachable only through the API gateway or from inside the service network.
 */
@RestController
@RequestMapping("/api/v1/internal/warehouses")
@RequiredArgsConstructor
public class InternalWarehouseController {

  private final WarehouseRepository warehouseRepository;

  /**
   * Returns 204 No Content if a warehouse with the given id exists, otherwise throws
   * EntityNotFoundException which is mapped to 404 by GlobalExceptionHandler.
   */
  @GetMapping("/{id}/exists")
  public ResponseEntity<Void> exists(@PathVariable UUID id) {
    if (!warehouseRepository.existsById(id)) {
      throw new EntityNotFoundException("Warehouse", id);
    }
    return ResponseEntity.noContent().build();
  }
}
