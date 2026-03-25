package com.wms.inventory.dto.zone;

import com.wms.inventory.entity.ZoneAvailability;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ZoneAvailabilityResponse(
    UUID id,
    UUID zoneId,
    LocalDate startDate,
    LocalDate endDate,
    Instant createdAt
) {
    public static ZoneAvailabilityResponse from(ZoneAvailability availability) {
        return new ZoneAvailabilityResponse(
            availability.getId(),
            availability.getZone().getId(),
            availability.getStartDate(),
            availability.getEndDate(),
            availability.getCreatedAt()
        );
    }
}
