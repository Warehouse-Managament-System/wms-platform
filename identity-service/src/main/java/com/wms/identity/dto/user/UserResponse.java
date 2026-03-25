package com.wms.identity.dto.user;

import com.wms.common.enums.UserRole;
import com.wms.common.enums.UserStatus;
import com.wms.identity.entity.User;
import java.time.Instant;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String email,
    String firstName,
    String lastName,
    UserRole role,
    UserStatus status,
    Instant createdAt,
    Instant updatedAt) {

  public static UserResponse from(User user) {
    return new UserResponse(
        user.getId(),
        user.getEmail(),
        user.getFirstName(),
        user.getLastName(),
        user.getRole(),
        user.getStatus(),
        user.getCreatedAt(),
        user.getUpdatedAt());
  }
}
