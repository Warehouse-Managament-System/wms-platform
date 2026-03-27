package com.wms.reservation.entity;

import com.wms.common.entity.BaseEntity;
import com.wms.common.enums.InvoiceStatus;
import com.wms.common.enums.InvoiceType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice extends BaseEntity {

  @Column(name = "customer_id", nullable = false)
  private UUID customerId;

  @Column(name = "warehouse_id", nullable = false)
  private UUID warehouseId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "booking_id")
  private Booking booking;

  @Column(name = "delivery_request_id")
  private UUID deliveryRequestId;

  @Enumerated(EnumType.STRING)
  @Column(name = "invoice_type", nullable = false, length = 20)
  private InvoiceType invoiceType;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal amount;

  @Builder.Default
  @Column(nullable = false, length = 3)
  private String currency = "USD";

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private InvoiceStatus status;

  @Column(name = "due_date", nullable = false)
  private LocalDate dueDate;
}
