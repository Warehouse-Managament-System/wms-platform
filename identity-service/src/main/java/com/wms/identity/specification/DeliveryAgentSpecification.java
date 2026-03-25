package com.wms.identity.specification;

import com.wms.common.enums.UserStatus;
import com.wms.identity.entity.DeliveryAgent;
import com.wms.identity.entity.User;
import jakarta.persistence.criteria.Join;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public final class DeliveryAgentSpecification {

  private DeliveryAgentSpecification() {}

  public static Specification<DeliveryAgent> hasWarehouseId(UUID warehouseId) {
    return (root, query, cb) -> cb.equal(root.get("warehouseId"), warehouseId);
  }

  public static Specification<DeliveryAgent> hasUserStatus(UserStatus status) {
    return (root, query, cb) -> {
      Join<DeliveryAgent, User> user = root.join("user");
      return cb.equal(user.get("status"), status);
    };
  }

  public static Specification<DeliveryAgent> hasTaxId(String taxId) {
    return (root, query, cb) -> cb.equal(root.get("taxId"), taxId);
  }

  public static Specification<DeliveryAgent> searchByName(String search) {
    return (root, query, cb) -> {
      Join<DeliveryAgent, User> user = root.join("user");
      String pattern = "%" + search.toLowerCase() + "%";
      return cb.or(
          cb.like(cb.lower(user.get("firstName")), pattern),
          cb.like(cb.lower(user.get("lastName")), pattern),
          cb.like(cb.lower(user.get("email")), pattern));
    };
  }

  public static Specification<DeliveryAgent> createdAfter(Instant from) {
    return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from);
  }

  public static Specification<DeliveryAgent> createdBefore(Instant to) {
    return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), to);
  }
}
