package com.wms.common.security;

import java.util.UUID;

public record UserContext(UUID userId, String userRole, String userEmail) {}
