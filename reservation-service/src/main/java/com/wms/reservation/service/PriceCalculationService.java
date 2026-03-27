package com.wms.reservation.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Service;

@Service
public class PriceCalculationService {

  public BigDecimal calculateTotalPrice(
      LocalDate startDate,
      LocalDate endDate,
      BigDecimal surfaceArea,
      BigDecimal dailyRate,
      BigDecimal weeklyRate,
      BigDecimal monthlyRate,
      int zoneDiscount,
      int warehouseDiscount) {

    long days = ChronoUnit.DAYS.between(startDate, endDate);

    BigDecimal rate;
    if (days >= 30) {
      rate = monthlyRate;
    } else if (days >= 7) {
      rate = weeklyRate;
    } else {
      rate = dailyRate;
    }

    BigDecimal base = surfaceArea.multiply(rate).multiply(BigDecimal.valueOf(days));

    base =
        base.multiply(BigDecimal.valueOf(100 - zoneDiscount))
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

    base =
        base.multiply(BigDecimal.valueOf(100 - warehouseDiscount))
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

    return base.setScale(2, RoundingMode.HALF_UP);
  }
}
