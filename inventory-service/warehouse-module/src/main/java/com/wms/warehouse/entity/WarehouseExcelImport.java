package com.wms.warehouse.entity;

import com.wms.common.enums.ImportStatus;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "warehouse_excel_imports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseExcelImport {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "warehouse_id", nullable = false)
  private Warehouse warehouse;

  @Column(name = "owner_id", nullable = false)
  private UUID ownerId;

  @Column(name = "file_name", nullable = false)
  private String fileName;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private ImportStatus status;

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

  @Column(name = "created_at", updatable = false)
  private Instant createdAt;

  @PrePersist
  protected void onCreate() {
    createdAt = Instant.now();
  }
}
