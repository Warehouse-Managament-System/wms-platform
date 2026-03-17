package com.wms.identity.specification;

import com.wms.common.enums.UserStatus;
import com.wms.identity.entity.Staff;
import com.wms.identity.entity.User;
import jakarta.persistence.criteria.Join;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public final class StaffSpecification {

  private StaffSpecification() {}

  public static Specification<Staff> hasWarehouseId(UUID warehouseId) {
    return (root, query, cb) -> cb.equal(root.get("warehouseId"), warehouseId);
  }

  public static Specification<Staff> hasUserStatus(UserStatus status) {
    return (root, query, cb) -> {
      Join<Staff, User> user = root.join("user");
      return cb.equal(user.get("status"), status);
    };
  }

  public static Specification<Staff> hasPosition(String position) {
    return (root, query, cb) ->
        cb.like(cb.lower(root.get("position")), "%" + position.toLowerCase() + "%");
  }

  public static Specification<Staff> searchByName(String search) {
    return (root, query, cb) -> {
      Join<Staff, User> user = root.join("user");
      String pattern = "%" + search.toLowerCase() + "%";
      return cb.or(
          cb.like(cb.lower(user.get("firstName")), pattern),
          cb.like(cb.lower(user.get("lastName")), pattern),
          cb.like(cb.lower(user.get("email")), pattern));
    };
  }

  public static Specification<Staff> createdAfter(Instant from) {
    return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from);
  }

  public static Specification<Staff> createdBefore(Instant to) {
    return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), to);
  }
}
