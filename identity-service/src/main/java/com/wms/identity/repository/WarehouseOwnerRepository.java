package com.wms.identity.repository;

import com.wms.identity.entity.WarehouseOwner;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface WarehouseOwnerRepository
    extends JpaRepository<WarehouseOwner, UUID>, JpaSpecificationExecutor<WarehouseOwner> {

  boolean existsByTaxIdAndDeletedAtIsNull(String taxId);

  boolean existsByTaxIdAndDeletedAtIsNullAndIdNot(String taxId, UUID id);

  @Query(
      """
      SELECT o FROM WarehouseOwner o
      JOIN FETCH o.user
      WHERE o.deletedAt IS NULL
      """)
  List<WarehouseOwner> findAllActive();

  @Query(
      """
      SELECT o FROM WarehouseOwner o
      JOIN FETCH o.user
      WHERE o.id = :id AND o.deletedAt IS NULL
      """)
  Optional<WarehouseOwner> findActiveById(UUID id);
}
