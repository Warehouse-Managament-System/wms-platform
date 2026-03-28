package com.wms.delivery.repository;

import com.wms.delivery.entity.DeliveryRequestItem;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRequestItemRepository extends JpaRepository<DeliveryRequestItem, UUID> {
  List<DeliveryRequestItem> findByDeliveryRequestId(UUID deliveryRequestId);

  Optional<DeliveryRequestItem> findByDeliveryRequestIdAndGoodsItemId(
      UUID deliveryRequestId, UUID goodsItemId);

  List<DeliveryRequestItem> findByGoodsItemId(UUID goodsItemId);
}
