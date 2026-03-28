package com.wms.delivery.repository;

import com.wms.delivery.entity.DeliveryRequest;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeliveryRequestRepository
    extends JpaRepository<DeliveryRequest, UUID>, JpaSpecificationExecutor<DeliveryRequest> {

  List<DeliveryRequest> findByCustomerId(UUID customerId);

  List<DeliveryRequest> findByBookingId(UUID bookingId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT dr FROM DeliveryRequest dr WHERE dr.id = :id")
  Optional<DeliveryRequest> findByIdForUpdate(@Param("id") UUID id);
}
