package com.wms.identity.service;

import com.wms.common.dto.PageResponse;
import com.wms.common.enums.UserRole;
import com.wms.common.enums.UserStatus;
import com.wms.common.exception.EntityNotFoundException;
import com.wms.common.exception.ResourceConflictException;
import com.wms.identity.dto.staff.CreateStaffRequest;
import com.wms.identity.dto.staff.StaffResponse;
import com.wms.identity.dto.staff.UpdateStaffRequest;
import com.wms.identity.entity.Staff;
import com.wms.identity.entity.User;
import com.wms.identity.feign.WarehouseClient;
import com.wms.identity.repository.StaffRepository;
import com.wms.identity.repository.UserRepository;
import com.wms.identity.specification.StaffSpecification;
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
public class StaffService {

  private final StaffRepository staffRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final WarehouseClient warehouseClient;

  @Transactional
  public StaffResponse create(CreateStaffRequest request) {
    if (userRepository.existsByEmailAndDeletedAtIsNull(request.email())) {
      throw new ResourceConflictException("Email already registered");
    }

    verifyWarehouseExists(request.warehouseId());

    User user =
        User.builder()
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .firstName(request.firstName())
            .lastName(request.lastName())
            .role(UserRole.STAFF)
            .status(UserStatus.ACTIVE)
            .build();
    userRepository.save(user);

    Staff staff =
        Staff.builder()
            .user(user)
            .warehouseId(request.warehouseId())
            .position(request.position())
            .build();

    return StaffResponse.from(staffRepository.save(staff));
  }

  @Transactional(readOnly = true)
  public StaffResponse getById(UUID id) {
    return StaffResponse.from(findByIdOrThrow(id));
  }

  @Transactional(readOnly = true)
  public StaffResponse getByUserId(UUID userId) {
    Staff staff =
        staffRepository
            .findByUserId(userId)
            .orElseThrow(
                () -> new EntityNotFoundException("Staff profile not found for user: " + userId));
    return StaffResponse.from(staff);
  }

  @Transactional(readOnly = true)
  public PageResponse<StaffResponse> search(
      String search,
      UUID warehouseId,
      UserStatus status,
      String position,
      Instant createdFrom,
      Instant createdTo,
      Pageable pageable) {

    Specification<Staff> spec = (root, query, cb) -> cb.conjunction();

    if (search != null && !search.isBlank()) {
      spec = spec.and(StaffSpecification.searchByName(search));
    }

    if (warehouseId != null) {
      spec = spec.and(StaffSpecification.hasWarehouseId(warehouseId));
    }

    if (status != null) {
      spec = spec.and(StaffSpecification.hasUserStatus(status));
    }

    if (position != null && !position.isBlank()) {
      spec = spec.and(StaffSpecification.hasPosition(position));
    }

    if (createdFrom != null) {
      spec = spec.and(StaffSpecification.createdAfter(createdFrom));
    }

    if (createdTo != null) {
      spec = spec.and(StaffSpecification.createdBefore(createdTo));
    }

    return PageResponse.from(staffRepository.findAll(spec, pageable).map(StaffResponse::from));
  }

  @Transactional
  public StaffResponse update(UUID id, UpdateStaffRequest request) {
    Staff staff = findByIdOrThrow(id);

    if (request.warehouseId() != null) {
      verifyWarehouseExists(request.warehouseId());
      staff.setWarehouseId(request.warehouseId());
    }

    if (request.position() != null) {
      staff.setPosition(request.position());
    }

    return StaffResponse.from(staffRepository.save(staff));
  }

  private void verifyWarehouseExists(UUID warehouseId) {
    try {
      warehouseClient.verifyExists(warehouseId);
    } catch (Exception ex) {
      throw new EntityNotFoundException("Warehouse", warehouseId);
    }
  }

  @Transactional
  public void delete(UUID id) {
    Staff staff = findByIdOrThrow(id);
    staff.getUser().setStatus(UserStatus.DEACTIVATED);
    staffRepository.delete(staff);
  }

  private Staff findByIdOrThrow(UUID id) {
    return staffRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Staff", id));
  }
}
