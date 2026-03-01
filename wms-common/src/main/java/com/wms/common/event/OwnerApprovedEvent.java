package com.wms.common.event;

import java.util.UUID;

public record OwnerApprovedEvent(UUID userId, String email, String companyName) {}
