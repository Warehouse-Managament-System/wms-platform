package com.wms.common.event;

import java.util.UUID;

public record BookingExpiredEvent(UUID bookingId, UUID clientId) {}
