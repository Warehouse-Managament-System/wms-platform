package com.wms.identity.service;

import com.wms.common.dto.PageResponse;
import com.wms.common.enums.UserRole;
import com.wms.common.enums.UserStatus;
import com.wms.common.exception.BusinessRuleException;
import com.wms.common.exception.EntityNotFoundException;
import com.wms.common.exception.ResourceConflictException;
import com.wms.identity.dto.deliveryagent.CreateDeliveryAgentRequest;
import com.wms.identity.dto.deliveryagent.DeliveryAgentResponse;
import com.wms.identity.dto.deliveryagent.UpdateDeliveryAgentRequest;
import com.wms.identity.entity.DeliveryAgent;
import com.wms.identity.entity.User;
import com.wms.identity.repository.DeliveryAgentRepository;
import com.wms.identity.repository.UserRepository;
import com.wms.identity.specification.DeliveryAgentSpecification;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryAgentService {

  private final DeliveryAgentRepository deliveryAgentRepository;
  private final UserRepository userRepository;

  @Transactional
  public DeliveryAgentResponse create(CreateDeliveryAgentRequest request) {
    User user =
        userRepository
            .findById(request.userId())
            .orElseThrow(() -> new EntityNotFoundException("User", request.userId()));

    if (user.getRole() != UserRole.DELIVERY_AGENT) {
      throw new BusinessRuleException(
          "User role must be DELIVERY_AGENT, but was " + user.getRole());
    }

    if (deliveryAgentRepository.existsByUserId(request.userId())) {
      throw new ResourceConflictException(
          "Delivery agent profile already exists for user: " + request.userId());
    }

    if (deliveryAgentRepository.existsByTaxId(request.taxId())) {
      throw new ResourceConflictException(
          "Delivery agent profile already exists with tax ID: " + request.taxId());
    }

    DeliveryAgent agent =
        DeliveryAgent.builder()
            .user(user)
            .warehouseId(request.warehouseId())
            .taxId(request.taxId())
            .vehicleInfo(request.vehicleInfo())
            .build();

    return DeliveryAgentResponse.from(deliveryAgentRepository.save(agent));
  }

  @Transactional(readOnly = true)
  public DeliveryAgentResponse getById(UUID id) {
    return DeliveryAgentResponse.from(findByIdOrThrow(id));
  }

  @Transactional(readOnly = true)
  public DeliveryAgentResponse getByUserId(UUID userId) {
    DeliveryAgent agent =
        deliveryAgentRepository
            .findByUserId(userId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "Delivery agent profile not found for user: " + userId));
    return DeliveryAgentResponse.from(agent);
  }

  @Transactional(readOnly = true)
  public PageResponse<DeliveryAgentResponse> search(
      String search,
      UUID warehouseId,
      UserStatus status,
      String taxId,
      Instant createdFrom,
      Instant createdTo,
      Pageable pageable) {

    Specification<DeliveryAgent> spec = Specification.where((Specification<DeliveryAgent>) null);

    if (search != null && !search.isBlank()) {
      spec = spec.and(DeliveryAgentSpecification.searchByName(search));
    }

    if (warehouseId != null) {
      spec = spec.and(DeliveryAgentSpecification.hasWarehouseId(warehouseId));
    }

    if (status != null) {
      spec = spec.and(DeliveryAgentSpecification.hasUserStatus(status));
    }

    if (taxId != null && !taxId.isBlank()) {
      spec = spec.and(DeliveryAgentSpecification.hasTaxId(taxId));
    }

    if (createdFrom != null) {
      spec = spec.and(DeliveryAgentSpecification.createdAfter(createdFrom));
    }

    if (createdTo != null) {
      spec = spec.and(DeliveryAgentSpecification.createdBefore(createdTo));
    }

    return PageResponse.from(
        deliveryAgentRepository.findAll(spec, pageable).map(DeliveryAgentResponse::from));
  }

  @Transactional
  public DeliveryAgentResponse update(UUID id, UpdateDeliveryAgentRequest request) {
    DeliveryAgent agent = findByIdOrThrow(id);

    if (request.warehouseId() != null) {
      agent.setWarehouseId(request.warehouseId());
    }

    if (request.taxId() != null) {
      if (!request.taxId().equals(agent.getTaxId())
          && deliveryAgentRepository.existsByTaxId(request.taxId())) {
        throw new ResourceConflictException(
            "Delivery agent profile already exists with tax ID: " + request.taxId());
      }
      agent.setTaxId(request.taxId());
    }

    if (request.vehicleInfo() != null) {
      agent.setVehicleInfo(request.vehicleInfo());
    }

    return DeliveryAgentResponse.from(deliveryAgentRepository.save(agent));
  }

  @Transactional
  public void delete(UUID id) {
    DeliveryAgent agent = findByIdOrThrow(id);
    agent.getUser().setStatus(UserStatus.DEACTIVATED);
    deliveryAgentRepository.delete(agent);
  }

  private DeliveryAgent findByIdOrThrow(UUID id) {
    return deliveryAgentRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("DeliveryAgent", id));
  }
}
