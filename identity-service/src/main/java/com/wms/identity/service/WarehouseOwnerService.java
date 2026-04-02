package com.wms.identity.service;

import com.wms.common.dto.PageResponse;
import com.wms.common.enums.UserRole;
import com.wms.common.enums.UserStatus;
import com.wms.common.event.KafkaTopics;
import com.wms.common.event.OwnerApprovedEvent;
import com.wms.common.event.OwnerRejectedEvent;
import com.wms.common.exception.EntityNotFoundException;
import com.wms.common.exception.ResourceConflictException;
import com.wms.common.outbox.OutboxPublisher;
import com.wms.identity.dto.request.CreateWarehouseOwnerRequest;
import com.wms.identity.dto.request.UpdateWarehouseOwnerRequest;
import com.wms.identity.dto.response.WarehouseOwnerResponse;
import com.wms.identity.entity.User;
import com.wms.identity.entity.WarehouseOwner;
import com.wms.identity.repository.UserRepository;
import com.wms.identity.repository.WarehouseOwnerRepository;
import com.wms.identity.specification.WarehouseOwnerSpecification;
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
public class WarehouseOwnerService {

  private final WarehouseOwnerRepository repository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final OutboxPublisher outboxPublisher;

  @Transactional
  public WarehouseOwnerResponse create(CreateWarehouseOwnerRequest request) {

    if (repository.existsByTaxIdAndDeletedAtIsNull(request.taxId())) {
      throw new ResourceConflictException("Tax ID already exists");
    }

    if (userRepository.existsByEmailAndDeletedAtIsNull(request.email())) {
      throw new ResourceConflictException("Email already registered");
    }

    User user =
        User.builder()
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .firstName(request.firstName())
            .lastName(request.lastName())
            .role(UserRole.WAREHOUSE_OWNER)
            .status(UserStatus.PENDING_APPROVAL)
            .build();
    userRepository.save(user);

    WarehouseOwner owner =
        WarehouseOwner.builder()
            .user(user)
            .companyName(request.companyName())
            .taxId(request.taxId())
            .address(request.address())
            .city(request.city())
            .country(request.country())
            .build();

    return WarehouseOwnerResponse.from(repository.save(owner));
  }

  @Transactional(readOnly = true)
  public WarehouseOwnerResponse get(UUID id) {
    return WarehouseOwnerResponse.from(findActiveOrThrow(id));
  }

  @Transactional(readOnly = true)
  public PageResponse<WarehouseOwnerResponse> search(
      String search, UserStatus status, Instant createdFrom, Instant createdTo, Pageable pageable) {

    Specification<WarehouseOwner> spec = WarehouseOwnerSpecification.isNotDeleted();

    if (search != null && !search.isBlank()) {
      spec = spec.and(WarehouseOwnerSpecification.searchByNameOrCompany(search));
    }

    if (status != null) {
      spec = spec.and(WarehouseOwnerSpecification.hasUserStatus(status));
    }

    if (createdFrom != null) {
      spec = spec.and(WarehouseOwnerSpecification.createdAfter(createdFrom));
    }

    if (createdTo != null) {
      spec = spec.and(WarehouseOwnerSpecification.createdBefore(createdTo));
    }

    return PageResponse.from(repository.findAll(spec, pageable).map(WarehouseOwnerResponse::from));
  }

  @Transactional
  public WarehouseOwnerResponse approve(UUID ownerId, UUID adminId) {

    WarehouseOwner owner = findActiveOrThrow(ownerId);

    User admin =
        userRepository
            .findById(adminId)
            .orElseThrow(() -> new EntityNotFoundException("Admin", adminId));

    owner.setApprovedBy(admin);
    owner.setApprovedAt(Instant.now());
    owner.setRejectionReason(null);
    owner.getUser().setStatus(UserStatus.ACTIVE);

    repository.save(owner);

    User user = owner.getUser();

    outboxPublisher.publish(
        "WarehouseOwner",
        owner.getId(),
        KafkaTopics.OWNER_APPROVED,
        new OwnerApprovedEvent(user.getId(), user.getEmail(), owner.getCompanyName()));

    return WarehouseOwnerResponse.from(owner);
  }

  @Transactional
  public WarehouseOwnerResponse reject(UUID ownerId, UUID adminId, String reason) {
    WarehouseOwner owner = findActiveOrThrow(ownerId);

    User admin =
        userRepository
            .findById(adminId)
            .orElseThrow(() -> new EntityNotFoundException("Admin", adminId));

    owner.setApprovedBy(admin);
    owner.setRejectionReason(reason);
    owner.setApprovedAt(null);
    owner.getUser().setStatus(UserStatus.DEACTIVATED);

    repository.save(owner);

    User user = owner.getUser();

    outboxPublisher.publish(
        "WarehouseOwner",
        owner.getId(),
        KafkaTopics.OWNER_REJECTED,
        new OwnerRejectedEvent(user.getId(), user.getEmail(), reason));

    return WarehouseOwnerResponse.from(owner);
  }

  @Transactional
  public WarehouseOwnerResponse update(UUID id, UpdateWarehouseOwnerRequest request) {
    WarehouseOwner owner = findActiveOrThrow(id);

    if (request.taxId() != null && !request.taxId().equals(owner.getTaxId())) {
      if (repository.existsByTaxIdAndDeletedAtIsNullAndIdNot(request.taxId(), id)) {
        throw new ResourceConflictException("Tax ID already in use");
      }

      owner.setTaxId(request.taxId());
    }

    User user = owner.getUser();
    if (request.firstName() != null) user.setFirstName(request.firstName());

    if (request.lastName() != null) user.setLastName(request.lastName());

    if (request.companyName() != null) owner.setCompanyName(request.companyName());

    if (request.address() != null) owner.setAddress(request.address());

    if (request.city() != null) owner.setCity(request.city());

    if (request.country() != null) owner.setCountry(request.country());

    return WarehouseOwnerResponse.from(repository.save(owner));
  }

  @Transactional
  public void delete(UUID id) {
    WarehouseOwner owner = findActiveOrThrow(id);
    Instant now = Instant.now();
    owner.setDeletedAt(now);
    owner.getUser().setDeletedAt(now);
  }

  private WarehouseOwner findActiveOrThrow(UUID id) {
    return repository
        .findActiveById(id)
        .orElseThrow(() -> new EntityNotFoundException("WarehouseOwner", id));
  }
}
