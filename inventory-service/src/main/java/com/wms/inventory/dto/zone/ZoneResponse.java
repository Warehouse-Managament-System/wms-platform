package com.wms.inventory.dto.zone;

import com.wms.common.enums.TemperatureType;
import com.wms.common.enums.ZoneStatus;
import com.wms.inventory.entity.Zone;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ZoneResponse(
    UUID id,
    UUID warehouseId,
    String name,
    String description,
    TemperatureType temperatureType,
    BigDecimal totalSurfaceArea,
    int discountPercentage,
    ZoneStatus status,
    Instant createdAt,
    Instant updatedAt) {
  public static ZoneResponse from(Zone zone) {
    return new ZoneResponse(
        zone.getId(),
        zone.getWarehouse().getId(),
        zone.getName(),
        zone.getDescription(),
        zone.getTemperatureType(),
        zone.getTotalSurfaceArea(),
        zone.getDiscountPercentage(),
        zone.getStatus(),
        zone.getCreatedAt(),
        zone.getUpdatedAt());
  }
}
