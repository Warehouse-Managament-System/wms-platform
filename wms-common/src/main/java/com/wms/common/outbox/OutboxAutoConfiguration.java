package com.wms.common.outbox;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

@AutoConfiguration
public class OutboxAutoConfiguration {

  @Bean
  @ConditionalOnBean(KafkaTemplate.class)
  public OutboxPoller outboxPoller(
      OutboxEventRepository outboxEventRepository, KafkaTemplate<Object, Object> kafkaTemplate) {
    return new OutboxPoller(outboxEventRepository, kafkaTemplate);
  }
}
