package com.wms.common.outbox;

import com.wms.common.entity.OutboxEvent;
import com.wms.common.enums.OutboxStatus;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPoller {

  private final OutboxEventRepository outboxEventRepository;
  private final KafkaTemplate<String, String> kafkaTemplate;

  @Scheduled(fixedDelayString = "${wms.outbox.poll-interval-ms:1000}")
  @Transactional
  public void pollAndPublish() {
    List<OutboxEvent> pendingEvents =
        outboxEventRepository.findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);

    for (OutboxEvent event : pendingEvents) {
      try {
        String topic = event.getAggregateType() + "." + event.getEventType();
        kafkaTemplate.send(topic, event.getAggregateId().toString(), event.getPayload());

        event.setStatus(OutboxStatus.PUBLISHED);
        event.setPublishedAt(Instant.now());
        outboxEventRepository.save(event);

        log.debug(
            "Outbox event published: topic={}, aggregateId={}", topic, event.getAggregateId());
      } catch (Exception e) {
        log.error("Failed to publish outbox event: id={}", event.getId(), e);
        event.setStatus(OutboxStatus.FAILED);
        outboxEventRepository.save(event);
      }
    }
  }
}
