package com.wms.goods.dto.receipt;

import com.wms.goods.entity.GoodsReceipt;

import java.time.Instant;
import java.util.UUID;

public record GoodsReceiptResponse(
    UUID id,
    UUID bookingId,
    UUID receivedBy,
    String inboundCarrier,
    String notes,
    Instant receivedAt
) {
    public static GoodsReceiptResponse from(GoodsReceipt entity) {
        return new GoodsReceiptResponse(
            entity.getId(),
            entity.getBookingId(),
            entity.getReceivedBy(),
            entity.getInboundCarrier(),
            entity.getNotes(),
            entity.getReceivedAt()
        );
    }
}
