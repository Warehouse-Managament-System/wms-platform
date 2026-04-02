package com.wms.reservation.repository;

import com.wms.common.enums.BookingStatus;
import com.wms.reservation.entity.Booking;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookingRepository
    extends JpaRepository<Booking, UUID>, JpaSpecificationExecutor<Booking> {

  List<Booking> findByCustomerIdAndDeletedAtIsNull(UUID customerId);

  Optional<Booking> findByIdAndDeletedAtIsNull(UUID id);

  List<Booking> findByWarehouseIdAndDeletedAtIsNull(UUID warehouseId);

  boolean existsByRoomIdAndStatusInAndDeletedAtIsNull(
      UUID roomId, Collection<BookingStatus> statuses);

  boolean existsByZoneIdAndStatusInAndDeletedAtIsNull(
      UUID zoneId, Collection<BookingStatus> statuses);

  Page<Booking> findByCustomerId(UUID customerId, Pageable pageable);

  Page<Booking> findByCustomerIdAndStatus(UUID customerId, BookingStatus status, Pageable pageable);

  Optional<Booking> findByIdAndCustomerIdAndDeletedAtIsNull(UUID id, UUID customerId);

  Page<Booking> findByWarehouseId(UUID warehouseId, Pageable pageable);

  Page<Booking> findByWarehouseIdAndStatus(
      UUID warehouseId, BookingStatus status, Pageable pageable);
}
