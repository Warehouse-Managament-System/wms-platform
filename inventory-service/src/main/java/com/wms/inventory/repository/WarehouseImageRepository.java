package com.wms.inventory.repository;

import com.wms.inventory.entity.WarehouseImage;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseImageRepository extends JpaRepository<WarehouseImage, UUID> {

  List<WarehouseImage> findByWarehouseId(UUID warehouseId);

  void deleteByWarehouseIdAndId(UUID warehouseId, UUID id);

  boolean existsByWarehouseIdAndIsPrimaryTrue(UUID warehouseId);
}
