package com.wms.reservation.repository;

import com.wms.reservation.entity.Invoice;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InvoiceRepository
    extends JpaRepository<Invoice, UUID>, JpaSpecificationExecutor<Invoice> {

  List<Invoice> findByCustomerId(UUID customerId);

  List<Invoice> findByWarehouseId(UUID warehouseId);

  Optional<Invoice> findByBookingId(UUID bookingId);
}
