package com.wms.identity.specification;

import com.wms.common.enums.UserStatus;
import com.wms.identity.entity.User;
import com.wms.identity.entity.WarehouseOwner;
import jakarta.persistence.criteria.Join;
import java.time.Instant;
import org.springframework.data.jpa.domain.Specification;

public final class WarehouseOwnerSpecification {

  private WarehouseOwnerSpecification() {}

  public static Specification<WarehouseOwner> isNotDeleted() {
    return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
  }

  public static Specification<WarehouseOwner> hasUserStatus(UserStatus status) {
    return (root, query, cb) -> {
      Join<WarehouseOwner, User> user = root.join("user");
      return cb.equal(user.get("status"), status);
    };
  }

  public static Specification<WarehouseOwner> searchByNameOrCompany(String search) {
    return (root, query, cb) -> {
      Join<WarehouseOwner, User> user = root.join("user");
      String pattern = "%" + search.toLowerCase() + "%";
      return cb.or(
          cb.like(cb.lower(user.get("firstName")), pattern),
          cb.like(cb.lower(user.get("lastName")), pattern),
          cb.like(cb.lower(user.get("email")), pattern),
          cb.like(cb.lower(root.get("companyName")), pattern));
    };
  }

  public static Specification<WarehouseOwner> createdAfter(Instant from) {
    return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from);
  }

  public static Specification<WarehouseOwner> createdBefore(Instant to) {
    return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), to);
  }
}
