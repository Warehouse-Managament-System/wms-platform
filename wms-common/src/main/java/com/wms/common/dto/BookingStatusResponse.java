package com.wms.common.dto;

import java.util.UUID;

public record BookingStatusResponse(UUID bookingId, String status, UUID clientId) {}
