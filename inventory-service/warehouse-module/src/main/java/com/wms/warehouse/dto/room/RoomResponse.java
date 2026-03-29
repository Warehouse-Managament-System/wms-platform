package com.wms.warehouse.dto.room;

import com.wms.common.enums.RoomStatus;
import com.wms.warehouse.entity.Room;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record RoomResponse(
    UUID id,
    UUID zoneId,
    String name,
    String description,
    BigDecimal totalSurfaceArea,
    BigDecimal pricePerSqmDaily,
    BigDecimal pricePerSqmWeekly,
    BigDecimal pricePerSqmMonthly,
    RoomStatus status,
    Instant createdAt,
    Instant updatedAt) {

  public static RoomResponse from(Room room) {
    return new RoomResponse(
        room.getId(),
        room.getZone().getId(),
        room.getName(),
        room.getDescription(),
        room.getTotalSurfaceArea(),
        room.getPricePerSqmDaily(),
        room.getPricePerSqmWeekly(),
        room.getPricePerSqmMonthly(),
        room.getStatus(),
        room.getCreatedAt(),
        room.getUpdatedAt());
  }
}
