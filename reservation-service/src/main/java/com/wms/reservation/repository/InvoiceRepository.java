package com.wms.reservation.repository;

import com.wms.common.enums.InvoiceStatus;
import com.wms.reservation.entity.Invoice;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InvoiceRepository
    extends JpaRepository<Invoice, UUID>, JpaSpecificationExecutor<Invoice> {

  Page<Invoice> findByCustomerId(UUID customerId, Pageable pageable);

  Page<Invoice> findByWarehouseId(UUID warehouseId, Pageable pageable);

  Optional<Invoice> findByBookingId(UUID bookingId);

  List<Invoice> findByDueDateBeforeAndStatus(LocalDate dueDate, InvoiceStatus status);
}
