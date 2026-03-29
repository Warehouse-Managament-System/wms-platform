package com.wms.warehouse.dto.room;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateRoomRequest(
    @NotBlank @Size(max = 100) String name,
    @NotBlank @Size(max = 255) String description,
    @NotNull @DecimalMin("0.01") BigDecimal totalSurfaceArea,
    @NotNull @DecimalMin("0.01") BigDecimal pricePerSqmDaily,
    @NotNull @DecimalMin("0.01") BigDecimal pricePerSqmWeekly,
    @NotNull @DecimalMin("0.01") BigDecimal pricePerSqmMonthly) {}
