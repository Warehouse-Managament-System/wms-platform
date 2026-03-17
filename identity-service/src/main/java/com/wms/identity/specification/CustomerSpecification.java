package com.wms.identity.specification;

import com.wms.common.enums.UserStatus;
import com.wms.identity.entity.Customer;
import com.wms.identity.entity.User;
import jakarta.persistence.criteria.Join;
import java.time.Instant;
import org.springframework.data.jpa.domain.Specification;

public final class CustomerSpecification {

  private CustomerSpecification() {}

  public static Specification<Customer> hasUserStatus(UserStatus status) {
    return (root, query, cb) -> {
      Join<Customer, User> user = root.join("user");
      return cb.equal(user.get("status"), status);
    };
  }

  public static Specification<Customer> searchByName(String search) {
    return (root, _, cb) -> {
      Join<Customer, User> user = root.join("user");
      String pattern = "%" + search.toLowerCase() + "%";
      return cb.or(
          cb.like(cb.lower(user.get("firstName")), pattern),
          cb.like(cb.lower(user.get("lastName")), pattern),
          cb.like(cb.lower(user.get("email")), pattern),
          cb.like(cb.lower(root.get("companyName")), pattern));
    };
  }

  public static Specification<Customer> hasTaxId(String taxId) {
    return (root, _, cb) -> cb.equal(root.get("taxId"), taxId);
  }

  public static Specification<Customer> createdAfter(Instant from) {
    return (root, _, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from);
  }

  public static Specification<Customer> createdBefore(Instant to) {
    return (root, _, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), to);
  }
}
