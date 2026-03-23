package com.wms.inventory.repository;

import com.wms.inventory.entity.WarehouseExcelImport;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseExcelImportRepository extends JpaRepository<WarehouseExcelImport, UUID> {}
