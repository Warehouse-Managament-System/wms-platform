package com.wms.inventory.dto.zone;

import com.wms.common.enums.TemperatureType;
import com.wms.common.enums.ZoneStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateZoneRequest(
    @Size(max = 100) String name,
    @Size(max = 255) String description,
    TemperatureType temperatureType,
    @DecimalMin("0.01") BigDecimal totalSurfaceArea,
    @Min(0) @Max(100) Integer discountPercentage,
    ZoneStatus status
) {
}
