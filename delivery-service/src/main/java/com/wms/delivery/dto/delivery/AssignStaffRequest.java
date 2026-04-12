package com.wms.delivery.dto.delivery;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AssignStaffRequest(@NotNull UUID staffId) {}
