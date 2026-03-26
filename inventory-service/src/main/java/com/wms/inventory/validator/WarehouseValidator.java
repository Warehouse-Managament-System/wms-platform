package com.wms.inventory.validator;

import com.wms.common.exception.BusinessRuleException;
import com.wms.inventory.entity.Warehouse;
import org.springframework.stereotype.Component;

@Component
public class WarehouseValidator {

  public void validatePublishable(Warehouse warehouse) {
    if (warehouse.getZones() == null || warehouse.getZones().isEmpty()) {
      throw new BusinessRuleException("Warehouse must have at least one zone to be published");
    }

    boolean hasRoom =
        warehouse.getZones().stream()
            .anyMatch(zone -> zone.getRooms() != null && !zone.getRooms().isEmpty());
    if (!hasRoom) {
      throw new BusinessRuleException("Warehouse must have at least one room to be published");
    }
  }
}
