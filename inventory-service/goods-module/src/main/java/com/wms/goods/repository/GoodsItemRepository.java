package com.wms.goods.repository;

import com.wms.goods.entity.GoodsItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GoodsItemRepository extends JpaRepository<GoodsItem, UUID>,
    JpaSpecificationExecutor<GoodsItem> {

    List<GoodsItem> findByGoodsImportId(UUID importId);

    Optional<GoodsItem> findByIdAndDeletedAtIsNull(UUID id);
}
