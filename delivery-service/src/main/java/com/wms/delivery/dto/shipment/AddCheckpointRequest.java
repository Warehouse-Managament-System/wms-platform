package com.wms.delivery.dto.shipment;

import com.wms.common.enums.ShipmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AddCheckpointRequest(
    @NotNull ShipmentStatus status, @NotBlank @Size(max = 255) String location, String note) {}
