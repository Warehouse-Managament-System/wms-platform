package com.wms.inventory.dto.zone;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ZoneAvailabilityRequest(@NotNull LocalDate startDate, @NotNull LocalDate endDate) {}
