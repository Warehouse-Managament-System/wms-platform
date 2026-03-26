package com.wms.inventory.repository;

import com.wms.inventory.entity.Category;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CategoryRepository
    extends JpaRepository<Category, UUID>, JpaSpecificationExecutor<Category> {
  boolean existsByName(String name);
}
