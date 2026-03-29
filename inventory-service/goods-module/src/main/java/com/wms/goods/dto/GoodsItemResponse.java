package com.wms.goods.dto;

import com.wms.goods.entity.GoodsItem;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record GoodsItemResponse(
    UUID id,
    UUID importId,
    String name,
    String sku,
    String barcode,
    BigDecimal quantity,
    String status,
    Instant createdAt
) {
    public static GoodsItemResponse from(GoodsItem entity) {
        return new GoodsItemResponse(
            entity.getId(),
            entity.getGoodsImport().getId(),
            entity.getName(),
            entity.getSku(),
            entity.getBarcode(),
            entity.getQuantity(),
            entity.getStatus().name(),
            entity.getCreatedAt()
        );
    }
}
