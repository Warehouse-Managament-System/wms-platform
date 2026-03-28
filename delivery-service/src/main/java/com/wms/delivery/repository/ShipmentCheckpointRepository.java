package com.wms.delivery.repository;

import com.wms.common.enums.ShipmentStatus;
import com.wms.delivery.entity.ShipmentCheckpoint;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentCheckpointRepository extends JpaRepository<ShipmentCheckpoint, UUID> {
  List<ShipmentCheckpoint> findByShipmentIdOrderByRecordedAtAsc(UUID shipmentId);

  Optional<ShipmentCheckpoint> findFirstByShipmentIdOrderByRecordedAtDesc(UUID shipmentId);

  boolean existsByShipmentIdAndStatus(UUID shipmentId, ShipmentStatus status);

  List<ShipmentCheckpoint> findByShipmentIdAndStatusOrderByRecordedAtAsc(
      UUID shipmentId, ShipmentStatus status);
}
