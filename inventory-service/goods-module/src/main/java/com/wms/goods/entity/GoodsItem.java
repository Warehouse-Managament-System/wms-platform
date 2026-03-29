package com.wms.goods.entity;

import com.wms.common.entity.SoftDeleteEntity;
import com.wms.common.enums.GoodsItemStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "goods_items")
public class GoodsItem extends SoftDeleteEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_import_id", nullable = false)
    private GoodsExcelImport goodsImport;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 100)
    private String sku;

    @Column(nullable = false, length = 100)
    private String barcode;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GoodsItemStatus status;
}
