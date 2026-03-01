package com.wms.common.kafka;

public interface IdempotencyChecker {

  boolean isDuplicate(String eventId);

  void markProcessed(String eventId);
}
