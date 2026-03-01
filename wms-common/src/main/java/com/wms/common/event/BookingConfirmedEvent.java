package com.wms.common.event;

import java.time.Instant;
import java.util.UUID;

public record BookingConfirmedEvent(
    UUID bookingId, UUID clientId, UUID roomId, Instant startDate, Instant endDate) {}
