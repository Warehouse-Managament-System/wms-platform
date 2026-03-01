package com.wms.common.dto;

import java.util.UUID;

public record RoomAvailabilityResponse(UUID roomId, boolean available, String reason) {}
