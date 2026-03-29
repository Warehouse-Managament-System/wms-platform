package com.wms.goods.repository;

import com.wms.goods.entity.GoodsReceipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GoodsReceiptRepository extends JpaRepository<GoodsReceipt, UUID> {
}
