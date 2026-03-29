package com.wms.warehouse.dto.warehouse;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateWarehouseRequest(
    @NotBlank String name,
    @NotBlank String description,
    @NotBlank String address,
    @NotBlank String city,
    @NotBlank String country,
    @NotNull @DecimalMin("-90") @DecimalMax("90") BigDecimal latitude,
    @NotNull @DecimalMin("-180") @DecimalMax("180") BigDecimal longitude,
    @NotNull @DecimalMin("0.01") BigDecimal totalSurfaceArea,
    @Min(0) @Max(100) Integer discountPercentage) {}
