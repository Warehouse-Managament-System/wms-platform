package com.wms.goods.dto;

import com.wms.goods.entity.GoodsExcelImport;

import java.time.Instant;
import java.util.UUID;

public record GoodsImportResponse(
    UUID id,
    UUID bookingId,
    UUID customerId,
    String fileName,
    String status,
    int totalRows,
    int successRows,
    int failedRows,
    Instant arrivalDeadline,
    Instant approvedAt,
    Instant createdAt
) {
    public static GoodsImportResponse from(GoodsExcelImport entity) {
        return new GoodsImportResponse(
            entity.getId(),
            entity.getBookingId(),
            entity.getCustomerId(),
            entity.getFileName(),
            entity.getStatus().name(),
            entity.getTotalRows(),
            entity.getSuccessRows(),
            entity.getFailedRows(),
            entity.getArrivalDeadline(),
            entity.getApprovedAt(),
            entity.getCreatedAt()
        );
    }
}
