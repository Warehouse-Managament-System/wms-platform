package com.wms.identity.service;

import com.wms.common.dto.PageResponse;
import com.wms.common.enums.UserRole;
import com.wms.common.enums.UserStatus;
import com.wms.common.exception.EntityNotFoundException;
import com.wms.common.exception.ResourceConflictException;
import com.wms.identity.dto.deliveryagent.CreateDeliveryAgentRequest;
import com.wms.identity.dto.deliveryagent.DeliveryAgentResponse;
import com.wms.identity.dto.deliveryagent.UpdateDeliveryAgentRequest;
import com.wms.identity.entity.DeliveryAgent;
import com.wms.identity.entity.User;
import com.wms.identity.feign.WarehouseClient;
import com.wms.identity.repository.DeliveryAgentRepository;
import com.wms.identity.repository.UserRepository;
import com.wms.identity.specification.DeliveryAgentSpecification;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryAgentService {

  private final DeliveryAgentRepository deliveryAgentRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final WarehouseClient warehouseClient;

  @Transactional
  public DeliveryAgentResponse create(CreateDeliveryAgentRequest request) {
    if (userRepository.existsByEmailAndDeletedAtIsNull(request.email())) {
      throw new ResourceConflictException("Email already registered");
    }

    if (deliveryAgentRepository.existsByTaxId(request.taxId())) {
      throw new ResourceConflictException(
          "Delivery agent profile already exists with tax ID: " + request.taxId());
    }

    verifyWarehouseExists(request.warehouseId());

    User user =
        User.builder()
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .firstName(request.firstName())
            .lastName(request.lastName())
            .role(UserRole.DELIVERY_AGENT)
            .status(UserStatus.ACTIVE)
            .build();
    userRepository.save(user);

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

    Specification<DeliveryAgent> spec = (root, query, cb) -> cb.conjunction();

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
      verifyWarehouseExists(request.warehouseId());
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

  private void verifyWarehouseExists(UUID warehouseId) {
    try {
      warehouseClient.verifyExists(warehouseId);
    } catch (Exception ex) {
      throw new EntityNotFoundException("Warehouse", warehouseId);
    }
  }
}
