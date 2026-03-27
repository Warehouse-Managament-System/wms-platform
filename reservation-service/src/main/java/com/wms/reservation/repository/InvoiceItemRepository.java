package com.wms.reservation.repository;

import com.wms.reservation.entity.InvoiceItem;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, UUID> {

  List<InvoiceItem> findByInvoiceId(UUID invoiceId);
}
