package com.wms.goods.repository;

import com.wms.goods.entity.GoodsExcelImport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GoodsExcelImportRepository extends JpaRepository<GoodsExcelImport, UUID> {

    List<GoodsExcelImport> findByCustomerId(UUID customerId);
}
