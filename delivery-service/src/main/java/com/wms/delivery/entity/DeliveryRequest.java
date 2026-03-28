package com.wms.delivery.entity;

import com.wms.common.entity.BaseEntity;
import com.wms.common.enums.DeliveryStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "delivery_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryRequest extends BaseEntity {

  @Column(name = "booking_id", nullable = false)
  private UUID bookingId;

  @Column(name = "customer_id", nullable = false)
  private UUID customerId;

  @Column(name = "destination_address", nullable = false, length = 255)
  private String destinationAddress;

  @Column(name = "destination_city", nullable = false, length = 100)
  private String destinationCity;

  @Column(name = "destination_country", nullable = false, length = 100)
  private String destinationCountry;

  @Column(name = "requested_date", nullable = false)
  private LocalDate requestedDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private DeliveryStatus status;
}
