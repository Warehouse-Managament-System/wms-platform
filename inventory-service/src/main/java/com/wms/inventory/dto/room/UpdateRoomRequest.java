package com.wms.inventory.dto.room;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record UpdateRoomRequest(
    @Size(max = 100) String name,
    @Size(max = 255) String description,
    @DecimalMin("0.01") BigDecimal totalSurfaceArea,
    @DecimalMin("0.01") BigDecimal pricePerSqmDaily,
    @DecimalMin("0.01") BigDecimal pricePerSqmWeekly,
    @DecimalMin("0.01") BigDecimal pricePerSqmMonthly) {}
