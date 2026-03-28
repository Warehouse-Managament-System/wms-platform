package com.wms.delivery.service;

import com.wms.common.enums.DeliveryStatus;
import com.wms.common.enums.ShipmentStatus;
import com.wms.common.outbox.OutboxPublisher;
import com.wms.delivery.dto.shipment.*;
import com.wms.delivery.entity.DeliveryRequest;
import com.wms.delivery.entity.Shipment;
import com.wms.delivery.entity.ShipmentCheckpoint;
import com.wms.delivery.repository.DeliveryRequestRepository;
import com.wms.delivery.repository.ShipmentCheckpointRepository;
import com.wms.delivery.repository.ShipmentRepository;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
            .orElseThrow(() -> new RuntimeException("Shipment not found"));

    if (!shipment.getClaimedBy().equals(agentId)) {
      throw new RuntimeException("Only the assigned agent can add checkpoints");
    }

    ShipmentStatus currentStatus = shipment.getStatus();
    ShipmentStatus nextStatus = request.status();

    // Validate status transition
    if (STATUS_FLOW.indexOf(nextStatus) != STATUS_FLOW.indexOf(currentStatus) + 1) {
      throw new RuntimeException("Invalid status transition");
    }

    // Create checkpoint
    ShipmentCheckpoint checkpoint =
        ShipmentCheckpoint.builder()
            .shipment(shipment)
            .status(nextStatus)
            .location(request.location())
            .note(request.note() != null ? request.note() : "")
            .recordedAt(Instant.now())
            .build();

    checkpointRepository.save(checkpoint);

    // Update shipment status
    shipment.setStatus(nextStatus);

    if (nextStatus == ShipmentStatus.COMPLETED) {
      shipment.setActualDeliveryDate(LocalDate.now());

      DeliveryRequest dr = shipment.getDeliveryRequest();
      dr.setStatus(DeliveryStatus.DELIVERED);
      deliveryRequestRepository.save(dr);
    }

    shipmentRepository.save(shipment);

    // Publish outbox event
    outboxPublisher.publish("shipment", checkpoint.getId(), "delivery.checkpoint", checkpoint);

    return ShipmentCheckpointResponse.from(checkpoint);
  }

  @Transactional
  public ShipmentTrackingResponse track(UUID shipmentId) {
    Shipment shipment =
        shipmentRepository
            .findById(shipmentId)
            .orElseThrow(() -> new RuntimeException("Shipment not found"));

    List<ShipmentCheckpointResponse> checkpoints =
        checkpointRepository.findByShipmentIdOrderByRecordedAtAsc(shipmentId).stream()
            .map(ShipmentCheckpointResponse::from)
            .collect(Collectors.toList());

    return new ShipmentTrackingResponse(ShipmentResponse.from(shipment), checkpoints);
  }

  @Transactional
  public ShipmentCheckpoint getLatestCheckpoint(UUID shipmentId) {
    return checkpointRepository
        .findFirstByShipmentIdOrderByRecordedAtDesc(shipmentId)
        .orElseThrow(() -> new RuntimeException("No checkpoints found for shipment"));
  }

  @Transactional
  public boolean isDelivered(UUID shipmentId) {
    return checkpointRepository.existsByShipmentIdAndStatus(shipmentId, ShipmentStatus.COMPLETED);
  }

  @Transactional
  public List<ShipmentCheckpointResponse> getCheckpointsByStatus(
      UUID shipmentId, ShipmentStatus status) {
    return checkpointRepository
        .findByShipmentIdAndStatusOrderByRecordedAtAsc(shipmentId, status)
        .stream()
        .map(ShipmentCheckpointResponse::from)
        .collect(Collectors.toList());
  }
}
