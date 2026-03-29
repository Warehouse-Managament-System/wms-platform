package com.wms.goods.entity;

import com.wms.common.enums.ReceiptCondition;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "goods_receipt_items")
public class GoodsReceiptItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_receipt_id", nullable = false)
    private GoodsReceipt goodsReceipt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_item_id", nullable = false)
    private GoodsItem goodsItem;

    @Column(name = "expected_qty", precision = 10, scale = 2)
    private BigDecimal expectedQty;

    @Column(name = "received_qty", precision = 10, scale = 2)
    private BigDecimal receivedQty;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ReceiptCondition condition;

    private String notes;
}
