package com.wms.delivery.repository;

import com.wms.common.enums.DeliveryNotificationStatus;
import com.wms.delivery.entity.DeliveryNotification;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryNotificationRepository extends JpaRepository<DeliveryNotification, UUID> {

  Optional<DeliveryNotification> findTopByDeliveryRequestIdOrderByNotifiedAtDesc(
      UUID deliveryRequestId);

  List<DeliveryNotification> findByStatus(DeliveryNotificationStatus status);
}
