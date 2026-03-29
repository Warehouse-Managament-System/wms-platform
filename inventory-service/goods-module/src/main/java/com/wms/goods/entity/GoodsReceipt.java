package com.wms.goods.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "goods_receipts")
public class GoodsReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "booking_id")
    private UUID bookingId; // cross-ref

    @Column(name = "received_by")
    private UUID receivedBy; // cross-ref

    @Column(name = "inbound_carrier")
    private String inboundCarrier;

    @Column(name = "received_at")
    private Instant receivedAt;

    private String notes;

    @PrePersist
    public void prePersist() {
        this.receivedAt = Instant.now();
    }
}
