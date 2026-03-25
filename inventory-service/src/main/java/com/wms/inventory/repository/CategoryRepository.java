package com.wms.inventory.repository;

import com.wms.inventory.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> , JpaSpecificationExecutor<Category> {
    boolean existsByName(String name);
}
