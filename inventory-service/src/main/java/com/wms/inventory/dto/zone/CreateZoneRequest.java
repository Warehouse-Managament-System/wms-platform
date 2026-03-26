package com.wms.inventory.dto.zone;

import com.wms.common.enums.TemperatureType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateZoneRequest(
    @NotBlank @Size(max = 100) String name,
    @NotBlank @Size(max = 255) String description,
    @NotNull TemperatureType temperatureType,
    @NotNull @DecimalMin("0.01") BigDecimal totalSurfaceArea,
    @Min(0) @Max(100) Integer discountPercentage) {}
