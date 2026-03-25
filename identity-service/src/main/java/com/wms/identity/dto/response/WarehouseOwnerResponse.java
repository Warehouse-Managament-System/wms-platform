package com.wms.identity.dto.response;

import com.wms.common.enums.UserStatus;
import com.wms.identity.entity.WarehouseOwner;
import java.time.Instant;
import java.util.UUID;

public record WarehouseOwnerResponse(
    UUID id,
    UUID userId,
    String firstName,
    String lastName,
    String email,
    UserStatus status,
    String companyName,
    String taxId,
    String address,
    String city,
    String country,
    String approvedByName,
    Instant approvedAt,
    String rejectionReason,
    Instant createdAt,
    Instant updatedAt) {

  public static WarehouseOwnerResponse from(WarehouseOwner owner) {
    var user = owner.getUser();
    var approvedBy = owner.getApprovedBy();
    return new WarehouseOwnerResponse(
        owner.getId(),
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        user.getEmail(),
        user.getStatus(),
        owner.getCompanyName(),
        owner.getTaxId(),
        owner.getAddress(),
        owner.getCity(),
        owner.getCountry(),
        approvedBy != null ? approvedBy.getFirstName() + " " + approvedBy.getLastName() : null,
        owner.getApprovedAt(),
        owner.getRejectionReason(),
        owner.getCreatedAt(),
        owner.getUpdatedAt());
  }
}
