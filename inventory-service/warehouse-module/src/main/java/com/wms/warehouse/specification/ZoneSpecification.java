package com.wms.warehouse.specification;

import com.wms.common.enums.TemperatureType;
import com.wms.common.enums.ZoneStatus;
import com.wms.warehouse.entity.Zone;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public class ZoneSpecification {

  private ZoneSpecification() {}

  public static Specification<Zone> hasWarehouseId(UUID warehouseId) {
    return ((root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("warehouse").get("id"), warehouseId));
  }

  public static Specification<Zone> hasTemperatureType(TemperatureType type) {
    return (root, query, cb) -> cb.equal(root.get("temperatureType"), type);
  }

  public static Specification<Zone> hasStatus(ZoneStatus status) {
    return (root, query, cb) -> cb.equal(root.get("status"), status);
  }
}
