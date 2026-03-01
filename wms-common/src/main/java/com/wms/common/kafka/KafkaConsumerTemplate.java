package com.wms.common.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;

@Slf4j
public abstract class KafkaConsumerTemplate<T> {

  private final IdempotencyChecker idempotencyChecker;

  protected KafkaConsumerTemplate(IdempotencyChecker idempotencyChecker) {
    this.idempotencyChecker = idempotencyChecker;
  }

  protected KafkaConsumerTemplate() {
    this.idempotencyChecker = null;
  }

  public void consume(ConsumerRecord<String, T> record) {
    String eventId = record.topic() + "-" + record.partition() + "-" + record.offset();
    log.debug(
        "Received event: topic={}, key={}, eventId={}", record.topic(), record.key(), eventId);

    if (idempotencyChecker != null && idempotencyChecker.isDuplicate(eventId)) {
      log.warn("Duplicate event detected, skipping: eventId={}", eventId);
      return;
    }

    try {
      handle(record.value());
      if (idempotencyChecker != null) {
        idempotencyChecker.markProcessed(eventId);
      }
    } catch (Exception e) {
      log.error("Error processing event: eventId={}", eventId, e);
      onError(record.value(), e);
    }
  }

  protected abstract void handle(T event);

  protected void onError(T event, Exception exception) {
    throw new RuntimeException("Failed to process event", exception);
  }
}
