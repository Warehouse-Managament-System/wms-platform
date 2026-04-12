package com.wms.common.event;

import java.time.Instant;
import java.util.UUID;

public record BookingExpirySoonEvent(UUID bookingId, UUID customerId, Instant expiryDate) {}
