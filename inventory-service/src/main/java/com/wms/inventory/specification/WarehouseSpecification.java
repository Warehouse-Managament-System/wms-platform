package com.wms.inventory.specification;

import com.wms.common.enums.WarehouseStatus;
import com.wms.inventory.entity.Warehouse;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public final class WarehouseSpecification {

  private WarehouseSpecification() {}

  public static Specification<Warehouse> hasOwnerId(UUID ownerId) {
    return (root, query, cb) -> cb.equal(root.get("ownerId"), ownerId);
  }

  public static Specification<Warehouse> hasStatus(WarehouseStatus status) {
    return (root, query, cb) -> cb.equal(root.get("status"), status);
  }

  public static Specification<Warehouse> searchByNameOrCity(String search) {
    return (root, query, cb) -> {
      String pattern = "%" + search.toLowerCase() + "%";
      return cb.or(
          cb.like(cb.lower(root.get("name")), pattern),
          cb.like(cb.lower(root.get("city")), pattern));
    };
  }

  public static Specification<Warehouse> createdAfter(Instant from) {
    return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from);
  }

  public static Specification<Warehouse> createdBefore(Instant to) {
    return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), to);
  }
}
