package com.wms.inventory.dto.warehouse;

import com.wms.common.enums.WarehouseStatus;
import com.wms.inventory.entity.Warehouse;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record WarehouseResponse(
    UUID id,
    UUID ownerId,
    String name,
    String description,
    String address,
    String city,
    String country,
    BigDecimal latitude,
    BigDecimal longitude,
    BigDecimal totalSurfaceArea,
    int discountPercentage,
    WarehouseStatus status,
    Instant createdAt,
    Instant updatedAt) {

  public static WarehouseResponse from(Warehouse warehouse) {
    return new WarehouseResponse(
        warehouse.getId(),
        warehouse.getOwnerId(),
        warehouse.getName(),
        warehouse.getDescription(),
        warehouse.getAddress(),
        warehouse.getCity(),
        warehouse.getCountry(),
        warehouse.getLatitude(),
        warehouse.getLongitude(),
        warehouse.getTotalSurfaceArea(),
        warehouse.getDiscountPercentage(),
        warehouse.getStatus(),
        warehouse.getCreatedAt(),
        warehouse.getUpdatedAt());
  }
}
