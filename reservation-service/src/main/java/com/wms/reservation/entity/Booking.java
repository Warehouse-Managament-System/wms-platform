package com.wms.reservation.entity;

import com.wms.common.entity.SoftDeleteEntity;
import com.wms.common.enums.BookingStatus;
import com.wms.common.enums.BookingType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking extends SoftDeleteEntity {

  @Column(name = "customer_id", nullable = false)
  private UUID customerId;

  @Column(name = "warehouse_id", nullable = false)
  private UUID warehouseId;

  @Enumerated(EnumType.STRING)
  @Column(name = "booking_type", nullable = false, length = 20)
  private BookingType bookingType;

  @Column(name = "room_id")
  private UUID roomId;

  @Column(name = "zone_id")
  private UUID zoneId;

  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @Column(name = "end_date", nullable = false)
  private LocalDate endDate;

  @Column(name = "surface_area", nullable = false, precision = 10, scale = 2)
  private BigDecimal surfaceArea;

  @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
  private BigDecimal totalPrice;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private BookingStatus status;
}
