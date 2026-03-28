package com.wms.delivery.service;

import com.wms.common.enums.DeliveryNotificationStatus;
import com.wms.common.enums.DeliveryStatus;
import com.wms.common.enums.ShipmentStatus;
import com.wms.common.event.DeliveryClaimedEvent;
import com.wms.common.event.DeliveryReadyEvent;
import com.wms.common.exception.BusinessRuleException;
import com.wms.common.exception.EntityNotFoundException;
import com.wms.common.outbox.OutboxPublisher;
import com.wms.delivery.entity.DeliveryNotification;
import com.wms.delivery.entity.DeliveryRequest;
import com.wms.delivery.entity.Shipment;
import com.wms.delivery.repository.DeliveryNotificationRepository;
import com.wms.delivery.repository.DeliveryRequestRepository;
import com.wms.delivery.repository.ShipmentRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AgentClaimService {

  private final DeliveryRequestRepository deliveryRequestRepository;
  private final DeliveryNotificationRepository deliveryNotificationRepository;
  private final ShipmentRepository shipmentRepository;
  private final OutboxPublisher outboxPublisher;

  @Transactional
  public void notifyAgents(UUID deliveryRequestId, UUID staffId) {
    DeliveryRequest deliveryRequest =
        deliveryRequestRepository
            .findById(deliveryRequestId)
            .orElseThrow(() -> new EntityNotFoundException("DeliveryRequest", deliveryRequestId));

    if (deliveryRequest.getStatus() != DeliveryStatus.PICKING) {
      throw new BusinessRuleException(
          "Delivery request must be in PICKING status to notify agents");
    }

    DeliveryNotification notification =
        DeliveryNotification.builder()
            .deliveryRequest(deliveryRequest)
            .notifiedBy(staffId)
            .status(DeliveryNotificationStatus.OPEN)
            .expiresAt(Instant.now().plus(24, ChronoUnit.HOURS))
            .build();

    deliveryNotificationRepository.save(notification);

    deliveryRequest.setStatus(DeliveryStatus.DISPATCHED);
    deliveryRequestRepository.save(deliveryRequest);

    outboxPublisher.publish(
        "DeliveryRequest",
        deliveryRequestId,
        "delivery.ready",
        new DeliveryReadyEvent(
            deliveryRequestId, deliveryRequest.getBookingId(), deliveryRequest.getCustomerId()));
  }

  @Transactional
  public void claim(UUID deliveryRequestId, UUID agentId) {
    DeliveryRequest deliveryRequest =
        deliveryRequestRepository
            .findByIdForUpdate(deliveryRequestId)
            .orElseThrow(() -> new EntityNotFoundException("DeliveryRequest", deliveryRequestId));

    if (deliveryRequest.getStatus() != DeliveryStatus.DISPATCHED) {
      throw new BusinessRuleException("Delivery is not available for claiming");
    }

    DeliveryNotification notification =
        deliveryNotificationRepository
            .findTopByDeliveryRequestIdOrderByNotifiedAtDesc(deliveryRequestId)
            .orElseThrow(
                () -> new BusinessRuleException("No notification found for this delivery"));

    if (notification.getStatus() != DeliveryNotificationStatus.OPEN) {
      throw new BusinessRuleException("Delivery has already been claimed");
    }

    notification.setStatus(DeliveryNotificationStatus.CLAIMED);
    deliveryNotificationRepository.save(notification);

    String trackingNumber = "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

    Shipment shipment =
        Shipment.builder()
            .deliveryRequest(deliveryRequest)
            .claimedBy(agentId)
            .claimedAt(Instant.now())
            .scheduledPickupTime(Instant.now().plus(2, ChronoUnit.HOURS))
            .trackingNumber(trackingNumber)
            .estimatedDeliveryDate(deliveryRequest.getRequestedDate())
            .status(ShipmentStatus.AWAITING_PICKUP)
            .build();

    shipment = shipmentRepository.save(shipment);

    outboxPublisher.publish(
        "DeliveryRequest",
        deliveryRequestId,
        "delivery.claimed",
        new DeliveryClaimedEvent(deliveryRequestId, agentId));
  }
}
