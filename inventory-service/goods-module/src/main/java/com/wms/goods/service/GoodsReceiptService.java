package com.wms.goods.service;

import com.wms.common.enums.GoodsItemStatus;
import com.wms.common.enums.ReceiptCondition;
import com.wms.common.event.GoodsDiscrepancyEvent;
import com.wms.common.outbox.OutboxPublisher;
import com.wms.goods.dto.receipt.*;
import com.wms.goods.entity.*;
import com.wms.goods.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoodsReceiptService {

    private final GoodsReceiptRepository receiptRepository;
    private final GoodsReceiptItemRepository receiptItemRepository;
    private final GoodsItemRepository goodsItemRepository;
    private final OutboxPublisher outboxPublisher;

    @Transactional
    public GoodsReceiptResponse createReceipt(UUID staffId, CreateGoodsReceiptRequest request) {

        GoodsReceipt receipt = GoodsReceipt.builder()
            .bookingId(request.bookingId())
            .receivedBy(staffId)
            .inboundCarrier(request.inboundCarrier())
            .notes(request.notes())
            .build();

        receiptRepository.save(receipt);

        return GoodsReceiptResponse.from(receipt);
    }

    @Transactional
    public void recordItem(UUID receiptId, UUID staffId, RecordReceiptItemRequest request) {

        GoodsReceipt receipt = receiptRepository.findById(receiptId)
            .orElseThrow(() -> new RuntimeException("Receipt not found"));

        GoodsItem item = goodsItemRepository
            .findByIdAndDeletedAtIsNull(request.goodsItemId())
            .orElseThrow(() -> new RuntimeException("Goods item not found"));

        boolean exists = receiptItemRepository
            .existsByGoodsReceiptIdAndGoodsItemId(receiptId, request.goodsItemId());

        if (exists) {
            throw new RuntimeException("Item already recorded for this receipt");
        }

        GoodsReceiptItem receiptItem = GoodsReceiptItem.builder()
            .goodsReceipt(receipt)
            .goodsItem(item)
            .expectedQty(request.expectedQty())
            .receivedQty(request.receivedQty())
            .condition(request.condition())
            .notes(request.notes())
            .build();

        receiptItemRepository.save(receiptItem);

        // Update item status
        if (request.condition() == ReceiptCondition.DAMAGED) {
            item.setStatus(GoodsItemStatus.DAMAGED);
        } else {
            item.setStatus(GoodsItemStatus.IN_WAREHOUSE);
        }

        goodsItemRepository.save(item);

        // Emit discrepancy event
        if (request.receivedQty().compareTo(request.expectedQty()) != 0) {
            UUID importId = item.getGoodsImport().getId();

            String details = "Expected: " + request.expectedQty()
                + ", Received: " + request.receivedQty();

            outboxPublisher.publish(
                "GoodsReceipt",
                receiptId,
                "goods.discrepancy",
                new GoodsDiscrepancyEvent(
                    importId,
                    null, // or warehouseId if you have it
                    details
                )
            );
        }
    }

    @Transactional(readOnly = true)
    public GoodsAvailableQtyResponse getAvailableQty(UUID goodsItemId) {

        GoodsItem item = goodsItemRepository
            .findByIdAndDeletedAtIsNull(goodsItemId)
            .orElseThrow(() -> new RuntimeException("Goods item not found"));

        return new GoodsAvailableQtyResponse(item.getQuantity());
    }
}
