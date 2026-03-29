package com.wms.warehouse.repository;

import com.wms.warehouse.entity.Warehouse;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WarehouseRepository
    extends JpaRepository<Warehouse, UUID>, JpaSpecificationExecutor<Warehouse> {}
