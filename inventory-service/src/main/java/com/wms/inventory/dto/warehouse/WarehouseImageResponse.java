package com.wms.inventory.dto.warehouse;

import com.wms.inventory.entity.WarehouseImage;
import java.time.Instant;
import java.util.UUID;

public record WarehouseImageResponse(
    UUID id, UUID warehouseId, String url, boolean isPrimary, Instant createdAt) {

  public static WarehouseImageResponse from(WarehouseImage image) {
    return new WarehouseImageResponse(
        image.getId(),
        image.getWarehouse().getId(),
        image.getUrl(),
        image.getIsPrimary(),
        image.getCreatedAt());
  }
}
