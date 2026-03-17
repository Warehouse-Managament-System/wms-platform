package com.wms.identity.service;

import com.wms.common.dto.PageResponse;
import com.wms.common.enums.UserRole;
import com.wms.common.enums.UserStatus;
import com.wms.common.exception.EntityNotFoundException;
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
    User user =
        userRepository
            .findByIdAndDeletedAtIsNull(userId)
            .orElseThrow(() -> new EntityNotFoundException("User", userId));
    return UserResponse.from(user);
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
    User user =
        userRepository
            .findByIdAndDeletedAtIsNull(userId)
            .orElseThrow(() -> new EntityNotFoundException("User", userId));
    user.setDeletedAt(Instant.now());
  }
}
