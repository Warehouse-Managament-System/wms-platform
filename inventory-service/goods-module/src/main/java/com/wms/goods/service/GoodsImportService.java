package com.wms.goods.service;

import com.wms.common.enums.GoodsImportStatus;
import com.wms.common.event.GoodsImportPendingEvent;
import com.wms.goods.dto.ApproveGoodsImportRequest;
import com.wms.goods.dto.RejectGoodsImportRequest;
import com.wms.goods.dto.GoodsImportResponse;
import com.wms.goods.entity.GoodsExcelImport;
import com.wms.goods.entity.GoodsItem;
import com.wms.goods.repository.*;
import com.wms.common.outbox.OutboxPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoodsImportService {

    private final GoodsExcelImportRepository importRepository;
    private final GoodsItemRepository itemRepository;
    private final GoodsExcelParserService parserService;
    private final OutboxPublisher outboxPublisher;

    @Transactional
    public GoodsImportResponse upload(UUID bookingId, UUID customerId, String fileName, InputStream stream) {

        GoodsExcelImport importRecord = GoodsExcelImport.builder()
            .bookingId(bookingId)
            .customerId(customerId)
            .fileName(fileName)
            .status(GoodsImportStatus.PENDING)
            .build();

        importRepository.save(importRecord);

        List<GoodsItem> items = parserService.parse(stream, importRecord);

        itemRepository.saveAll(items);

        importRepository.save(importRecord);

        outboxPublisher.publish(
            "GoodsImport",
            importRecord.getId(),
            "goods.import.pending",
            new GoodsImportPendingEvent(
                importRecord.getId(),
                importRecord.getWarehouseId(),
                importRecord.getCustomerId(),
                importRecord.getSuccessRows()
            )
        );

        return GoodsImportResponse.from(importRecord);
    }

    @Transactional
    public void approve(UUID id, UUID ownerId, ApproveGoodsImportRequest request) {

        GoodsExcelImport importRecord = importRepository.findById(id)
            .orElseThrow();

        if (!importRecord.getStatus().equals(GoodsImportStatus.PENDING)) {
            throw new IllegalStateException("Invalid status");
        }

        importRecord.setStatus(GoodsImportStatus.APPROVED);
        importRecord.setApprovedBy(ownerId);
        importRecord.setApprovedAt(Instant.now());
        importRecord.setArrivalDeadline(request.arrivalDeadline());

        importRepository.save(importRecord);

        outboxPublisher.publish("GoodsImport", id, "goods.approved", null);
    }

    @Transactional
    public void reject(UUID id, UUID ownerId, RejectGoodsImportRequest request) {

        GoodsExcelImport importRecord = importRepository.findById(id)
            .orElseThrow();

        if (!importRecord.getStatus().equals(GoodsImportStatus.PENDING)) {
            throw new IllegalStateException("Invalid status");
        }

        importRecord.setStatus(GoodsImportStatus.REJECTED);

        importRepository.save(importRecord);

        outboxPublisher.publish("GoodsImport", id, "goods.rejected", request.reason());
    }
}
