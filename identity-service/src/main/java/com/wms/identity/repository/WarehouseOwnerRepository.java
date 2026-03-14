package com.wms.identity.repository;

import com.wms.identity.entity.WarehouseOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WarehouseOwnerRepository extends JpaRepository<WarehouseOwner, UUID> {

    Optional<WarehouseOwner> findByIdAndDeletedAtIsNull(UUID id);

    List<WarehouseOwner> findByDeletedAtIsNull();

    boolean existsByTaxIdAndDeletedAtIsNull(String taxId);

    @Query("""
        SELECT o
        FROM WarehouseOwner o
        JOIN FETCH o.user
        WHERE o.deletedAt IS NULL
    """)
    List<WarehouseOwner> findAllActive();

    @Query("""
        SELECT o
        FROM WarehouseOwner o
        JOIN FETCH o.user
        WHERE o.id = :id AND o.deletedAt IS NULL
    """)
    Optional<WarehouseOwner> findActiveById(UUID id);

}
