package com.wms.identity.service;

import com.wms.common.dto.PageResponse;
import com.wms.common.enums.UserRole;
import com.wms.common.enums.UserStatus;
import com.wms.common.exception.EntityNotFoundException;
import com.wms.identity.dto.user.UpdateUserProfileRequest;
import com.wms.identity.dto.user.UserResponse;
import com.wms.identity.entity.User;
import com.wms.identity.repository.UserRepository;
import com.wms.identity.specification.UserSpecification;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  @Transactional(readOnly = true)
  public UserResponse getUserById(UUID userId) {
    return UserResponse.from(findActiveOrThrow(userId));
  }

  @Transactional(readOnly = true)
  public UserResponse getUserByEmail(String email) {

    User user =
        userRepository
            .findByEmailAndDeletedAtIsNull(email)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

    return UserResponse.from(user);
  }

  @Transactional
  public UserResponse updateProfile(String email, UpdateUserProfileRequest request) {
    User user =
        userRepository
            .findByEmailAndDeletedAtIsNull(email)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

    if (request.firstName() != null) user.setFirstName(request.firstName());
    if (request.lastName() != null) user.setLastName(request.lastName());

    return UserResponse.from(userRepository.save(user));
  }

  @Transactional(readOnly = true)
  public PageResponse<UserResponse> search(
      String search,
      UserRole role,
      UserStatus status,
      Instant createdFrom,
      Instant createdTo,
      Pageable pageable) {

    Specification<User> spec = UserSpecification.isNotDeleted();

    if (search != null && !search.isBlank()) {
      spec = spec.and(UserSpecification.searchByNameOrEmail(search));
    }

    if (role != null) {
      spec = spec.and(UserSpecification.hasRole(role));
    }

    if (status != null) {
      spec = spec.and(UserSpecification.hasStatus(status));
    }

    if (createdFrom != null) {
      spec = spec.and(UserSpecification.createdAfter(createdFrom));
    }

    if (createdTo != null) {
      spec = spec.and(UserSpecification.createdBefore(createdTo));
    }

    return PageResponse.from(userRepository.findAll(spec, pageable).map(UserResponse::from));
  }

  @Transactional
  public void deleteUser(UUID userId) {
    User user = findActiveOrThrow(userId);
    user.setDeletedAt(Instant.now());
  }

  private User findActiveOrThrow(UUID userId) {
    return userRepository
        .findByIdAndDeletedAtIsNull(userId)
        .orElseThrow(() -> new EntityNotFoundException("User", userId));
  }
}
