package com.wms.inventory.service;

import com.wms.common.enums.ImportStatus;
import com.wms.common.enums.TemperatureType;
import com.wms.common.enums.ZoneStatus;
import com.wms.common.exception.BusinessRuleException;
import com.wms.common.exception.EntityNotFoundException;
import com.wms.inventory.dto.excelimport.WarehouseExcelImportResponse;
import com.wms.inventory.entity.Warehouse;
import com.wms.inventory.entity.WarehouseExcelImport;
import com.wms.inventory.repository.WarehouseExcelImportRepository;
import com.wms.inventory.repository.WarehouseRepository;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseExcelService {

  private final WarehouseRepository warehouseRepository;
  private final WarehouseExcelImportRepository importRepository;

  public byte[] generateTemplate() {
    try (Workbook workbook = new XSSFWorkbook()) {
      CellStyle headerStyle = createHeaderStyle(workbook);

      Sheet zonesSheet = workbook.createSheet("Zones");

      createHeaderRow(
          zonesSheet,
          headerStyle,
          "name",
          "description",
          "temperatureType (AMBIENT/REFRIGERATED/FROZEN)",
          "totalSurfaceArea",
          "status (ACTIVE/MAINTENANCE/INACTIVE)");

      zonesSheet.setColumnWidth(0, 6000);
      zonesSheet.setColumnWidth(1, 8000);
      zonesSheet.setColumnWidth(2, 10000);
      zonesSheet.setColumnWidth(3, 5000);
      zonesSheet.setColumnWidth(4, 8000);

      Sheet roomsSheet = workbook.createSheet("Rooms");

      createHeaderRow(
          roomsSheet,
          headerStyle,
          "zoneName",
          "name",
          "description",
          "totalSurfaceArea",
          "pricePerSqmDaily",
          "pricePerSqmWeekly",
          "pricePerSqmMonthly");

      for (int i = 0; i < 7; i++) roomsSheet.setColumnWidth(i, 5000);

      Sheet categoriesSheet = workbook.createSheet("Categories");

      createHeaderRow(
          categoriesSheet,
          headerStyle,
          "name",
          "description",
          "assignTo (ZONE/ROOM)",
          "assignName");

      for (int i = 0; i < 4; i++) categoriesSheet.setColumnWidth(i, 6000);

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      workbook.write(out);

      return out.toByteArray();
    } catch (IOException e) {
      throw new BusinessRuleException("Failed to generate Excel template");
    }
  }

  @Transactional
  public WarehouseExcelImportResponse processImport(
      UUID warehouseId, UUID ownerId, String fileName, InputStream excelStream) {

    Warehouse warehouse =
        warehouseRepository
            .findById(warehouseId)
            .orElseThrow(() -> new EntityNotFoundException("Warehouse", warehouseId));

    if (!warehouse.getOwnerId().equals(ownerId)) {
      throw new EntityNotFoundException("Warehouse", warehouseId);
    }

    WarehouseExcelImport importRecord =
        WarehouseExcelImport.builder()
            .warehouse(warehouse)
            .ownerId(ownerId)
            .fileName(fileName)
            .status(ImportStatus.PROCESSING)
            .build();
    importRepository.save(importRecord);

    List<String> errors = new ArrayList<>();
    int totalRows = 0;
    int successRows = 0;
    int failedRows = 0;

    try (Workbook workbook = new XSSFWorkbook(excelStream)) {

      Sheet zonesSheet = workbook.getSheet("Zones");

      if (zonesSheet != null) {
        for (int i = 1; i <= zonesSheet.getLastRowNum(); i++) {
          Row row = zonesSheet.getRow(i);

          if (row == null || isRowEmpty(row)) continue;

          totalRows++;

          try {
            validateZoneRow(row, i);
            successRows++;
          } catch (Exception e) {
            failedRows++;
            errors.add("Zones row " + (i + 1) + ": " + e.getMessage());
          }
        }
      }

      Sheet roomsSheet = workbook.getSheet("Rooms");

      if (roomsSheet != null) {
        for (int i = 1; i <= roomsSheet.getLastRowNum(); i++) {

          Row row = roomsSheet.getRow(i);

          if (row == null || isRowEmpty(row)) continue;

          totalRows++;

          try {
            validateRoomRow(row, i);
            successRows++;
          } catch (Exception e) {
            failedRows++;
            errors.add("Rooms row " + (i + 1) + ": " + e.getMessage());
          }
        }
      }

      Sheet categoriesSheet = workbook.getSheet("Categories");
      if (categoriesSheet != null) {
        for (int i = 1; i <= categoriesSheet.getLastRowNum(); i++) {

          Row row = categoriesSheet.getRow(i);

          if (row == null || isRowEmpty(row)) continue;

          totalRows++;

          try {
            validateCategoryRow(row, i);
            successRows++;
          } catch (Exception e) {
            failedRows++;
            errors.add("Categories row " + (i + 1) + ": " + e.getMessage());
          }
        }
      }

    } catch (IOException e) {
      importRecord.setStatus(ImportStatus.FAILED);
      importRecord.setErrorFileUrl("Invalid Excel file: " + e.getMessage());
      importRepository.save(importRecord);
      throw new BusinessRuleException("Failed to read Excel file");
    }

    importRecord.setTotalRows(totalRows);
    importRecord.setSuccessRows(successRows);
    importRecord.setFailedRows(failedRows);
    importRecord.setStatus(failedRows == 0 ? ImportStatus.COMPLETED : ImportStatus.FAILED);

    if (!errors.isEmpty()) {
      importRecord.setErrorFileUrl(String.join("; ", errors));
    }

    importRepository.save(importRecord);

    log.info(
        "Excel import completed for warehouse {}: total={}, success={}, failed={}",
        warehouseId,
        totalRows,
        successRows,
        failedRows);

    return WarehouseExcelImportResponse.from(importRecord);
  }

  private void validateZoneRow(Row row, int rowIndex) {
    String name = getStringCell(row, 0);
    String description = getStringCell(row, 1);
    String tempType = getStringCell(row, 2);
    double area = getNumericCell(row, 3);
    String status = getStringCell(row, 4);

    if (name == null || name.isBlank()) throw new IllegalArgumentException("name is required");
    if (description == null || description.isBlank()) {
      throw new IllegalArgumentException("description is required");
    }

    try {
      TemperatureType.valueOf(tempType);
    } catch (Exception e) {
      throw new IllegalArgumentException("invalid temperatureType: " + tempType);
    }

    if (area <= 0) throw new IllegalArgumentException("totalSurfaceArea must be > 0");

    try {
      ZoneStatus.valueOf(status);
    } catch (Exception e) {
      throw new IllegalArgumentException("invalid status: " + status);
    }
  }

  private void validateRoomRow(Row row, int rowIndex) {
    String zoneName = getStringCell(row, 0);
    String name = getStringCell(row, 1);
    String description = getStringCell(row, 2);
    double area = getNumericCell(row, 3);
    double priceDaily = getNumericCell(row, 4);
    double priceWeekly = getNumericCell(row, 5);
    double priceMonthly = getNumericCell(row, 6);

    if (zoneName == null || zoneName.isBlank()) {
      throw new IllegalArgumentException("zoneName is required");
    }

    if (name == null || name.isBlank()) throw new IllegalArgumentException("name is required");

    if (description == null || description.isBlank()) {
      throw new IllegalArgumentException("description is required");
    }

    if (area <= 0) throw new IllegalArgumentException("totalSurfaceArea must be > 0");
    if (priceDaily <= 0) throw new IllegalArgumentException("pricePerSqmDaily must be > 0");
    if (priceWeekly <= 0) throw new IllegalArgumentException("pricePerSqmWeekly must be > 0");
    if (priceMonthly <= 0) throw new IllegalArgumentException("pricePerSqmMonthly must be > 0");
  }

  private void validateCategoryRow(Row row, int rowIndex) {
    String name = getStringCell(row, 0);
    String description = getStringCell(row, 1);
    String assignTo = getStringCell(row, 2);
    String assignName = getStringCell(row, 3);

    if (name == null || name.isBlank()) throw new IllegalArgumentException("name is required");

    if (description == null || description.isBlank()) {
      throw new IllegalArgumentException("description is required");
    }

    if (!"ZONE".equals(assignTo) && !"ROOM".equals(assignTo)) {
      throw new IllegalArgumentException("assignTo must be ZONE or ROOM");
    }

    if (assignName == null || assignName.isBlank()) {
      throw new IllegalArgumentException("assignName is required");
    }
  }

  private String getStringCell(Row row, int col) {
    Cell cell = row.getCell(col);
    if (cell == null) return null;

    return switch (cell.getCellType()) {
      case STRING -> cell.getStringCellValue().trim();
      case NUMERIC -> BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString();
      default -> null;
    };
  }

  private double getNumericCell(Row row, int col) {
    Cell cell = row.getCell(col);
    if (cell == null) return 0;

    return switch (cell.getCellType()) {
      case NUMERIC -> cell.getNumericCellValue();
      case STRING -> {
        try {
          yield Double.parseDouble(cell.getStringCellValue().trim());
        } catch (NumberFormatException e) {
          yield 0;
        }
      }
      default -> 0;
    };
  }

  private boolean isRowEmpty(Row row) {
    for (int i = 0; i < row.getLastCellNum(); i++) {
      Cell cell = row.getCell(i);
      if (cell != null && cell.getCellType() != CellType.BLANK) return false;
    }

    return true;
  }

  private CellStyle createHeaderStyle(Workbook workbook) {
    CellStyle style = workbook.createCellStyle();
    Font font = workbook.createFont();
    font.setBold(true);
    style.setFont(font);
    style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    return style;
  }

  private void createHeaderRow(Sheet sheet, CellStyle style, String... headers) {
    Row row = sheet.createRow(0);
    for (int i = 0; i < headers.length; i++) {
      Cell cell = row.createCell(i);
      cell.setCellValue(headers[i]);
      cell.setCellStyle(style);
    }
  }
}
