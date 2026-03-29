package com.wms.goods.service;

import com.wms.goods.entity.GoodsExcelImport;
import com.wms.goods.entity.GoodsItem;
import com.wms.common.enums.GoodsItemStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GoodsExcelParserService {

    public List<GoodsItem> parse(InputStream excelStream, GoodsExcelImport importRecord) {
        List<GoodsItem> items = new ArrayList<>();

        int totalRows = 0;
        int successRows = 0;
        int failedRows = 0;

        try (XSSFWorkbook workbook = new XSSFWorkbook(excelStream)) {
            var sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                totalRows++;
                var row = sheet.getRow(i);

                try {
                    String name = getStringCell(row, 0);
                    String sku = getStringCell(row, 1);
                    String barcode = getStringCell(row, 2);
                    BigDecimal quantity = getNumericCell(row, 3);

                    if (isBlank(name) || isBlank(sku) || isBlank(barcode) || quantity.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new IllegalArgumentException("Invalid data");
                    }

                    GoodsItem item = GoodsItem.builder()
                        .goodsImport(importRecord)
                        .name(name)
                        .sku(sku)
                        .barcode(barcode)
                        .quantity(quantity)
                        .status(GoodsItemStatus.PENDING)
                        .build();

                    items.add(item);
                    successRows++;

                } catch (Exception e) {
                    failedRows++;
                    log.error("Row {} failed: {}", i, e.getMessage());
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Excel", e);
        }

        importRecord.setTotalRows(totalRows);
        importRecord.setSuccessRows(successRows);
        importRecord.setFailedRows(failedRows);

        return items;
    }

    private String getStringCell(org.apache.poi.ss.usermodel.Row row, int index) {
        var cell = row.getCell(index);
        return cell != null ? cell.getStringCellValue().trim() : "";
    }

    private BigDecimal getNumericCell(org.apache.poi.ss.usermodel.Row row, int index) {
        var cell = row.getCell(index);
        return cell != null ? BigDecimal.valueOf(cell.getNumericCellValue()) : BigDecimal.ZERO;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
