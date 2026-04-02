package com.wms.delivery.service;

import com.wms.common.enums.DeliveryStatus;
import com.wms.common.enums.ShipmentStatus;
import com.wms.common.event.DeliveryCheckpointEvent;
import com.wms.common.event.KafkaTopics;
import com.wms.common.exception.BusinessRuleException;
import com.wms.common.exception.EntityNotFoundException;
import com.wms.common.outbox.OutboxPublisher;
import com.wms.delivery.dto.shipment.*;
import com.wms.delivery.entity.DeliveryRequest;
import com.wms.delivery.entity.Shipment;
import com.wms.delivery.entity.ShipmentCheckpoint;
import com.wms.delivery.repository.DeliveryRequestRepository;
import com.wms.delivery.repository.ShipmentCheckpointRepository;
import com.wms.delivery.repository.ShipmentRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShipmentService {

  private final ShipmentRepository shipmentRepository;
  private final ShipmentCheckpointRepository checkpointRepository;
  private final DeliveryRequestRepository deliveryRequestRepository;
  private final OutboxPublisher outboxPublisher;

  private static final List<ShipmentStatus> STATUS_FLOW =
      List.of(
          ShipmentStatus.AWAITING_PICKUP,
          ShipmentStatus.PICKED_UP,
          ShipmentStatus.ON_THE_WAY,
          ShipmentStatus.COMPLETED);

  @Transactional
  public ShipmentCheckpointResponse addCheckpoint(
      UUID shipmentId, UUID agentId, AddCheckpointRequest request) {
    Shipment shipment =
        shipmentRepository
            .findById(shipmentId)
            .orElseThrow(() -> new EntityNotFoundException("Shipment", shipmentId));

    if (!shipment.getClaimedBy().equals(agentId)) {
      throw new BusinessRuleException("Only the assigned agent can add checkpoints");
    }

    ShipmentStatus currentStatus = shipment.getStatus();
    ShipmentStatus nextStatus = request.status();

    if (STATUS_FLOW.indexOf(nextStatus) != STATUS_FLOW.indexOf(currentStatus) + 1) {
      throw new BusinessRuleException("Invalid status transition");
    }

    ShipmentCheckpoint checkpoint =
        ShipmentCheckpoint.builder()
            .shipment(shipment)
            .status(nextStatus)
            .location(request.location())
            .note(request.note() != null ? request.note() : "")
            .build();

    checkpointRepository.save(checkpoint);

    shipment.setStatus(nextStatus);

    DeliveryRequest dr = shipment.getDeliveryRequest();
    if (nextStatus == ShipmentStatus.PICKED_UP) {
      dr.setStatus(DeliveryStatus.IN_TRANSIT);
      deliveryRequestRepository.save(dr);
    } else if (nextStatus == ShipmentStatus.COMPLETED) {
      shipment.setActualDeliveryDate(LocalDate.now());
      dr.setStatus(DeliveryStatus.DELIVERED);
      deliveryRequestRepository.save(dr);
    }

    shipmentRepository.save(shipment);

    outboxPublisher.publish(
        "Shipment",
        shipmentId,
        KafkaTopics.DELIVERY_CHECKPOINT,
        new DeliveryCheckpointEvent(
            shipmentId, nextStatus.name(), request.location(), shipment.getTrackingNumber()));

    return ShipmentCheckpointResponse.from(checkpoint);
  }

  @Transactional(readOnly = true)
  public ShipmentTrackingResponse track(UUID shipmentId) {
    Shipment shipment =
        shipmentRepository
            .findById(shipmentId)
            .orElseThrow(() -> new EntityNotFoundException("Shipment", shipmentId));

    List<ShipmentCheckpointResponse> checkpoints =
        checkpointRepository.findByShipmentIdOrderByRecordedAtAsc(shipmentId).stream()
            .map(ShipmentCheckpointResponse::from)
            .toList();

    return new ShipmentTrackingResponse(ShipmentResponse.from(shipment), checkpoints);
  }

  @Transactional(readOnly = true)
  public List<ShipmentResponse> listByAgent(UUID agentId) {
    return shipmentRepository.findByClaimedBy(agentId).stream()
        .map(ShipmentResponse::from)
        .toList();
  }
}
