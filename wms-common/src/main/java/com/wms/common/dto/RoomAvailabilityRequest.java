package com.wms.common.dto;

import java.time.Instant;
import java.util.UUID;

public record RoomAvailabilityRequest(UUID roomId, Instant startDate, Instant endDate) {}
