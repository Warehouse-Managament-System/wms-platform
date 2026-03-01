package com.wms.common.event;

import java.util.UUID;

public record OwnerRejectedEvent(UUID userId, String email, String reason) {}
