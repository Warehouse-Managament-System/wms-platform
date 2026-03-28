package com.wms.delivery.dto.shipment;

import java.util.List;

public record ShipmentTrackingResponse(
    ShipmentResponse shipment, List<ShipmentCheckpointResponse> checkpoints) {}
