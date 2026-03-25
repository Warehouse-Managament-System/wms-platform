package com.wms.inventory.dto.warehouse;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record UpdateWarehouseRequest(
    String name,
    String description,
    String address,
    String city,
    String country,
    @DecimalMin("-90") @DecimalMax("90") BigDecimal latitude,
    @DecimalMin("-180") @DecimalMax("180") BigDecimal longitude,
    @DecimalMin("0.01") BigDecimal totalSurfaceArea,
    @Min(0) @Max(100) Integer discountPercentage) {}
