package com.wms.identity.specification;

import com.wms.common.enums.UserRole;
import com.wms.common.enums.UserStatus;
import com.wms.identity.entity.User;
import java.time.Instant;
import org.springframework.data.jpa.domain.Specification;

public final class UserSpecification {

  private UserSpecification() {}

  public static Specification<User> isNotDeleted() {
    return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
  }

  public static Specification<User> hasRole(UserRole role) {
    return (root, query, cb) -> cb.equal(root.get("role"), role);
  }

  public static Specification<User> hasStatus(UserStatus status) {
    return (root, query, cb) -> cb.equal(root.get("status"), status);
  }

  public static Specification<User> searchByNameOrEmail(String search) {
    return (root, query, cb) -> {
      String pattern = "%" + search.toLowerCase() + "%";
      return cb.or(
          cb.like(cb.lower(root.get("firstName")), pattern),
          cb.like(cb.lower(root.get("lastName")), pattern),
          cb.like(cb.lower(root.get("email")), pattern));
    };
  }

  public static Specification<User> createdAfter(Instant from) {
    return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from);
  }

  public static Specification<User> createdBefore(Instant to) {
    return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), to);
  }
}
