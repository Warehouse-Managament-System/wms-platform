package com.wms.inventory.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wms.common.enums.GoodsItemStatus;
import com.wms.common.enums.RoomStatus;
import com.wms.common.event.KafkaTopics;
import com.wms.goods.entity.GoodsExcelImport;
import com.wms.goods.entity.GoodsItem;
import com.wms.goods.repository.GoodsExcelImportRepository;
import com.wms.goods.repository.GoodsItemRepository;
import com.wms.warehouse.repository.RoomRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventListener {

  private final RoomRepository roomRepository;
  private final GoodsItemRepository goodsItemRepository;
  private final GoodsExcelImportRepository goodsExcelImportRepository;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = KafkaTopics.BOOKING_CONFIRMED, groupId = "inventory-events")
  @Transactional
  public void onBookingConfirmed(String payload) {
    JsonNode node = parse(payload);
    UUID roomId = uuid(node, "roomId");

    roomRepository
        .findByIdAndDeletedAtIsNull(roomId)
        .ifPresentOrElse(
            room -> {
              room.setStatus(RoomStatus.BOOKED);
              roomRepository.save(room);
              log.info("Room {} marked as BOOKED for booking {}", roomId, text(node, "bookingId"));
            },
            () -> log.warn("Room {} not found for booking confirmation", roomId));
  }

  @KafkaListener(topics = KafkaTopics.BOOKING_CANCELLED, groupId = "inventory-events")
  @Transactional
  public void onBookingCancelled(String payload) {
    JsonNode node = parse(payload);
    UUID roomId = uuid(node, "roomId");
    releaseRoom(roomId, text(node, "bookingId"));
  }

  @KafkaListener(topics = KafkaTopics.BOOKING_EXPIRED, groupId = "inventory-events")
  @Transactional
  public void onBookingExpired(String payload) {
    JsonNode node = parse(payload);
    UUID roomId = uuid(node, "roomId");
    releaseRoom(roomId, text(node, "bookingId"));
  }

  @KafkaListener(topics = KafkaTopics.DELIVERY_ACKNOWLEDGED, groupId = "inventory-events")
  @Transactional
  public void onDeliveryAcknowledged(String payload) {
    JsonNode node = parse(payload);
    UUID bookingId = uuid(node, "bookingId");

    List<GoodsExcelImport> imports = goodsExcelImportRepository.findByBookingId(bookingId);
    int updated = 0;
    for (GoodsExcelImport imp : imports) {
      List<GoodsItem> items = goodsItemRepository.findByGoodsImportId(imp.getId());
      for (GoodsItem item : items) {
        if (item.getStatus() == GoodsItemStatus.IN_WAREHOUSE) {
          item.setStatus(GoodsItemStatus.DELIVERED);
          goodsItemRepository.save(item);
          updated++;
        }
      }
    }
    log.info(
        "Delivery acknowledged for booking {}: {} goods items marked DELIVERED",
        bookingId,
        updated);
  }

  private void releaseRoom(UUID roomId, String bookingId) {
    roomRepository
        .findByIdAndDeletedAtIsNull(roomId)
        .ifPresentOrElse(
            room -> {
              if (room.getStatus() == RoomStatus.BOOKED) {
                room.setStatus(RoomStatus.AVAILABLE);
                roomRepository.save(room);
                log.info(
                    "Room {} released to AVAILABLE after booking {} cancelled/expired",
                    roomId,
                    bookingId);
              }
            },
            () -> log.warn("Room {} not found for booking {} release", roomId, bookingId));
  }

  private JsonNode parse(String payload) {
    try {
      return objectMapper.readTree(payload);
    } catch (Exception e) {
      log.error("Failed to parse event payload: {}", payload, e);
      throw new IllegalArgumentException("Failed to parse event payload", e);
    }
  }

  private UUID uuid(JsonNode node, String field) {
    JsonNode fieldNode = node.get(field);
    if (fieldNode == null || fieldNode.isNull()) {
      throw new IllegalArgumentException(
          "Required UUID field '%s' missing from event payload".formatted(field));
    }
    return UUID.fromString(fieldNode.asText());
  }

  private String text(JsonNode node, String field) {
    JsonNode fieldNode = node.get(field);
    return fieldNode != null ? fieldNode.asText() : "";
  }
}
