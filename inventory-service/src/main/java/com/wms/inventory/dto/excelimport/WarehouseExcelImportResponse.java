package com.wms.inventory.dto.excelimport;

import com.wms.common.enums.ImportStatus;
import com.wms.inventory.entity.WarehouseExcelImport;
import java.time.Instant;
import java.util.UUID;

public record WarehouseExcelImportResponse(
    UUID id,
    UUID warehouseId,
    String fileName,
    ImportStatus status,
    int totalRows,
    int successRows,
    int failedRows,
    String errorFileUrl,
    Instant createdAt) {

  public static WarehouseExcelImportResponse from(WarehouseExcelImport e) {
    return new WarehouseExcelImportResponse(
        e.getId(),
        e.getWarehouse().getId(),
        e.getFileName(),
        e.getStatus(),
        e.getTotalRows(),
        e.getSuccessRows(),
        e.getFailedRows(),
        e.getErrorFileUrl(),
        e.getCreatedAt());
  }
}
