package com.wms.common.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wms.common.entity.OutboxEvent;
import com.wms.common.enums.OutboxStatus;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

  private final OutboxEventRepository outboxEventRepository;
  private final ObjectMapper objectMapper;

  @Transactional
  public void publish(String aggregateType, UUID aggregateId, String eventType, Object payload) {
    OutboxEvent event =
        OutboxEvent.builder()
            .aggregateType(aggregateType)
            .aggregateId(aggregateId)
            .eventType(eventType)
            .payload(serialize(payload))
            .status(OutboxStatus.PENDING)
            .build();

    outboxEventRepository.save(event);
    log.debug("Outbox event saved: type={}, aggregateId={}", eventType, aggregateId);
  }

  private String serialize(Object payload) {
    try {
      return objectMapper.writeValueAsString(payload);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Failed to serialize outbox event payload", e);
    }
  }
}
