package com.wms.common.event;

import java.util.UUID;

public record BookingCancelledEvent(UUID bookingId, UUID customerId, UUID roomId, String reason) {}
