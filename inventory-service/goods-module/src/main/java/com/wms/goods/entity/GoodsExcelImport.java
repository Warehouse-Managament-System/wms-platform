package com.wms.goods.entity;

import com.wms.common.enums.GoodsImportStatus;
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
@Table(name = "goods_excel_imports")
public class GoodsExcelImport {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) private UUID id;

    @Column(name = "booking_id", nullable = false)
    private UUID bookingId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "warehouse_id")
    private UUID warehouseId;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GoodsImportStatus status;

    @Builder.Default
    @Column(name = "total_rows", nullable = false)
    private int totalRows = 0;

    @Builder.Default
    @Column(name = "success_rows", nullable = false)
    private int successRows = 0;

    @Builder.Default
    @Column(name = "failed_rows", nullable = false)
    private int failedRows = 0;

    @Builder.Default
    @Column(name = "error_file_url", nullable = false)
    private String errorFileUrl = "";

    @Column(name = "arrival_deadline")
    private Instant arrivalDeadline;

    @Column(name = "approved_by")
    private UUID approvedBy;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }
}
