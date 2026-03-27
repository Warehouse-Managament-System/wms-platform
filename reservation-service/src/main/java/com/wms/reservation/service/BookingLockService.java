package com.wms.reservation.service;

import com.wms.common.exception.BusinessRuleException;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingLockService {

  private final StringRedisTemplate redisTemplate;

  public String acquireLock(String resourceType, UUID resourceId, Duration timeout) {
    String lockKey = "lock:booking:" + resourceType + ":" + resourceId;
    String lockValue = UUID.randomUUID().toString();

    Boolean acquired =
        redisTemplate
            .opsForValue()
            .setIfAbsent(lockKey, lockValue, timeout.toMillis(), TimeUnit.MILLISECONDS);

    if (acquired == null || !acquired) {
      throw new BusinessRuleException(
          "Resource is currently being booked by another user. Try again.");
    }

    return lockKey;
  }

  public void releaseLock(String lockKey) {
    if (lockKey != null) {
      redisTemplate.delete(lockKey);
    }
  }
}
