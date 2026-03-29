package com.wms.warehouse.repository;

import com.wms.warehouse.entity.Zone;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, UUID>, JpaSpecificationExecutor<Zone> {
  List<Zone> findByWarehouseId(UUID warehouseId);
}
