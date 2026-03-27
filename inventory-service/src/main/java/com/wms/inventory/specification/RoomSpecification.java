package com.wms.inventory.specification;

import com.wms.common.enums.RoomStatus;
import com.wms.inventory.entity.Room;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public class RoomSpecification {

  private RoomSpecification() {}

  public static Specification<Room> hasZoneId(UUID zoneId) {
    return (root, query, cb) -> cb.equal(root.get("zone").get("id"), zoneId);
  }

  public static Specification<Room> hasStatus(RoomStatus status) {
    return (root, query, cb) -> cb.equal(root.get("status"), status);
  }

  public static Specification<Room> isNotDeleted() {
    return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
  }
}
