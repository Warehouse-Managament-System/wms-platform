package com.wms.identity.repository;

import com.wms.identity.entity.WarehouseOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OwnerProfileRepository
    extends JpaRepository<WarehouseOwner, UUID> {


}
