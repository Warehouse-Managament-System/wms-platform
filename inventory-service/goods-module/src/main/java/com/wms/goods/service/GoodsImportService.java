package com.wms.goods.service;

import com.wms.common.enums.GoodsImportStatus;
import com.wms.common.event.GoodsApprovedEvent;
import com.wms.common.event.GoodsImportPendingEvent;
import com.wms.common.event.GoodsRejectedEvent;
import com.wms.common.event.KafkaTopics;
import com.wms.common.exception.BusinessRuleException;
import com.wms.common.exception.EntityNotFoundException;
import com.wms.common.exception.UnauthorizedException;
import com.wms.goods.dto.ApproveGoodsImportRequest;
import com.wms.goods.dto.GoodsItemResponse;
import com.wms.goods.dto.RejectGoodsImportRequest;
import com.wms.goods.dto.GoodsImportResponse;
import com.wms.goods.entity.GoodsExcelImport;
import com.wms.goods.entity.GoodsItem;
import com.wms.goods.feign.BookingClient;
import com.wms.goods.feign.dto.BookingStatusResponse;
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
    private final BookingClient bookingClient;

    @Transactional
    public GoodsImportResponse upload(UUID bookingId, UUID customerId, String fileName, InputStream stream) {

        BookingStatusResponse booking = fetchBookingOrThrow(bookingId);

        if (!booking.customerId().equals(customerId)) {
            throw new UnauthorizedException(
                "Booking does not belong to the requesting customer");
        }

        GoodsExcelImport importRecord = GoodsExcelImport.builder()
            .bookingId(bookingId)
            .customerId(customerId)
            .warehouseId(booking.warehouseId())
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
            KafkaTopics.GOODS_IMPORT_PENDING,
            new GoodsImportPendingEvent(
                importRecord.getId(),
                importRecord.getWarehouseId(),
                importRecord.getCustomerId(),
                items.size()
            )
        );

        return GoodsImportResponse.from(importRecord);
    }

    @Transactional
    public void approve(UUID id, UUID ownerId, ApproveGoodsImportRequest request) {

        GoodsExcelImport importRecord = importRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("GoodsExcelImport", id));

        if (!importRecord.getStatus().equals(GoodsImportStatus.PENDING)) {
            throw new BusinessRuleException("Import must be in PENDING status to approve");
        }

        importRecord.setStatus(GoodsImportStatus.APPROVED);
        importRecord.setApprovedBy(ownerId);
        importRecord.setApprovedAt(Instant.now());
        importRecord.setArrivalDeadline(request.arrivalDeadline());

        importRepository.save(importRecord);

        outboxPublisher.publish(
            "GoodsImport",
            id,
            KafkaTopics.GOODS_APPROVED,
            new GoodsApprovedEvent(id, importRecord.getWarehouseId(), ownerId));
    }

    @Transactional
    public void reject(UUID id, UUID ownerId, RejectGoodsImportRequest request) {

        GoodsExcelImport importRecord = importRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("GoodsExcelImport", id));

        if (!importRecord.getStatus().equals(GoodsImportStatus.PENDING)) {
            throw new BusinessRuleException("Import must be in PENDING status to reject");
        }

        importRecord.setStatus(GoodsImportStatus.REJECTED);

        importRepository.save(importRecord);

        outboxPublisher.publish(
            "GoodsImport",
            id,
            KafkaTopics.GOODS_REJECTED,
            new GoodsRejectedEvent(id, importRecord.getWarehouseId(), request.reason()));
    }

    @Transactional(readOnly = true)
    public List<GoodsImportResponse> listAll() {
        return importRepository.findAll().stream()
            .map(GoodsImportResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<GoodsImportResponse> listByCustomer(UUID customerId) {
        return importRepository.findByCustomerId(customerId).stream()
            .map(GoodsImportResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<GoodsImportResponse> listApproved() {
        return importRepository.findByStatus(GoodsImportStatus.APPROVED).stream()
            .map(GoodsImportResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<GoodsItemResponse> listItemsByBooking(UUID bookingId) {
        List<GoodsExcelImport> imports = importRepository.findByBookingId(bookingId);
        return imports.stream()
            .flatMap(imp -> itemRepository.findByGoodsImportId(imp.getId()).stream())
            .map(GoodsItemResponse::from)
            .toList();
    }

    private BookingStatusResponse fetchBookingOrThrow(UUID bookingId) {
        try {
            return bookingClient.getStatus(bookingId);
        } catch (Exception ex) {
            throw new EntityNotFoundException("Booking", bookingId);
        }
    }
}
