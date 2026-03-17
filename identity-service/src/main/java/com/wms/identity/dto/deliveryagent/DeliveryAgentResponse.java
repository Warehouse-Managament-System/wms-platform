package com.wms.identity.dto.deliveryagent;

import com.wms.common.enums.UserRole;
import com.wms.common.enums.UserStatus;
import com.wms.identity.entity.DeliveryAgent;
import java.time.Instant;
import java.util.UUID;

public record DeliveryAgentResponse(
    UUID id,
    UUID userId,
    String firstName,
    String lastName,
    String email,
    UserRole role,
    UserStatus status,
    UUID warehouseId,
    String taxId,
    String vehicleInfo,
    Instant createdAt,
    Instant updatedAt) {

  public static DeliveryAgentResponse from(DeliveryAgent agent) {
    var user = agent.getUser();

    return new DeliveryAgentResponse(
        agent.getId(),
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        user.getEmail(),
        user.getRole(),
        user.getStatus(),
        agent.getWarehouseId(),
        agent.getTaxId(),
        agent.getVehicleInfo(),
        agent.getCreatedAt(),
        agent.getUpdatedAt());
  }
}
