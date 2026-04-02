package com.wms.reservation.dto;

import com.wms.common.enums.BookingStatus;
import com.wms.reservation.entity.Booking;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public class BookingSpecification {
  private BookingSpecification() {}

  public static Specification<Booking> hasCustomerId(UUID customerId) {
    return (root, query, cb) -> cb.equal(root.get("customerId"), customerId);
  }

  public static Specification<Booking> hasWarehouseId(UUID warehouseId) {
    return (root, query, cb) -> cb.equal(root.get("warehouseId"), warehouseId);
  }

  public static Specification<Booking> hasStatus(BookingStatus status) {
    return (root, query, cb) -> cb.equal(root.get("status"), status);
  }

  public static Specification<Booking> isNotDeleted() {
    return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
  }
}
