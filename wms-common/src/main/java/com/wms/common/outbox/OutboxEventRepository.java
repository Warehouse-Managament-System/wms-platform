package com.wms.common.outbox;

import com.wms.common.entity.OutboxEvent;
import com.wms.common.enums.OutboxStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

  List<OutboxEvent> findByStatusOrderByCreatedAtAsc(OutboxStatus status);
}
