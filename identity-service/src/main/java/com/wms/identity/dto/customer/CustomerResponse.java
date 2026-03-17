package com.wms.identity.dto.customer;

import com.wms.common.enums.UserRole;
import com.wms.common.enums.UserStatus;
import com.wms.identity.entity.Customer;
import java.time.Instant;
import java.util.UUID;

public record CustomerResponse(
    UUID id,
    UUID userId,
    String firstName,
    String lastName,
    String email,
    UserRole role,
    UserStatus status,
    String companyName,
    String taxId,
    String contactPersonName,
    Instant createdAt,
    Instant updatedAt) {

  public static CustomerResponse from(Customer customer) {
    var user = customer.getUser();
    return new CustomerResponse(
        customer.getId(),
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        user.getEmail(),
        user.getRole(),
        user.getStatus(),
        customer.getCompanyName(),
        customer.getTaxId(),
        customer.getContactPersonName(),
        customer.getCreatedAt(),
        customer.getUpdatedAt());
  }
}
