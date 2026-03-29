package com.wms.goods.repository;

import com.wms.goods.entity.GoodsReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GoodsReceiptItemRepository extends JpaRepository<GoodsReceiptItem, UUID> {

    boolean existsByGoodsReceiptIdAndGoodsItemId(UUID receiptId, UUID itemId);
}
