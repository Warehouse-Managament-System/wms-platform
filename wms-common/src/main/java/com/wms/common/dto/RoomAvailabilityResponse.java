package com.wms.common.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record RoomAvailabilityResponse(
    UUID roomId,
    boolean available,
    String reason,
    BigDecimal surfaceArea,
    BigDecimal pricePerSqmDaily,
    BigDecimal pricePerSqmWeekly,
    BigDecimal pricePerSqmMonthly,
    int zoneDiscountPercentage,
    int warehouseDiscountPercentage) {}
