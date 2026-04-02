package com.wms.delivery.repository;

import com.wms.delivery.entity.Shipment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentRepository extends JpaRepository<Shipment, UUID> {

  Optional<Shipment> findByDeliveryRequestId(UUID deliveryRequestId);

  Optional<Shipment> findByTrackingNumber(String trackingNumber);

  List<Shipment> findByClaimedBy(UUID agentId);
}
