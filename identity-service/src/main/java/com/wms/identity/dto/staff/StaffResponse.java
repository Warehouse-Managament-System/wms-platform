package com.wms.identity.dto.staff;

import com.wms.common.enums.UserRole;
import com.wms.common.enums.UserStatus;
import com.wms.identity.entity.Staff;
import java.time.Instant;
import java.util.UUID;

public record StaffResponse(
    UUID id,
    UUID userId,
    String firstName,
    String lastName,
    String email,
    UserRole role,
    UserStatus status,
    UUID warehouseId,
    String position,
    Instant createdAt,
    Instant updatedAt) {

  public static StaffResponse from(Staff staff) {
    var user = staff.getUser();
    return new StaffResponse(
        staff.getId(),
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        user.getEmail(),
        user.getRole(),
        user.getStatus(),
        staff.getWarehouseId(),
        staff.getPosition(),
        staff.getCreatedAt(),
        staff.getUpdatedAt());
  }
}
