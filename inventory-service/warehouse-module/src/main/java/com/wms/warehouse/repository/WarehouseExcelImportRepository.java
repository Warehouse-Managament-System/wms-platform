package com.wms.warehouse.repository;

import com.wms.warehouse.entity.WarehouseExcelImport;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseExcelImportRepository extends JpaRepository<WarehouseExcelImport, UUID> {}
