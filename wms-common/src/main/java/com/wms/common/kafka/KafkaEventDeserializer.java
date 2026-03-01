package com.wms.common.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import org.apache.kafka.common.serialization.Deserializer;

public class KafkaEventDeserializer<T> implements Deserializer<T> {

  private final ObjectMapper objectMapper;
  private final Class<T> targetType;

  public KafkaEventDeserializer(Class<T> targetType) {
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());
    this.targetType = targetType;
  }

  @Override
  public T deserialize(String topic, byte[] data) {
    if (data == null) return null;
    try {
      return objectMapper.readValue(data, targetType);
    } catch (IOException e) {
      throw new IllegalArgumentException("Failed to deserialize event from topic: " + topic, e);
    }
  }
}
