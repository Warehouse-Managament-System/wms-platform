package com.wms.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PriceCalculationService unit tests")
class PriceCalculationServiceTest {

  private final PriceCalculationService service = new PriceCalculationService();

  @Test
  @DisplayName("uses the daily rate for short bookings of less than a week")
  void usesDailyRate_forShortBookings() {
    BigDecimal total =
        service.calculateTotalPrice(
            LocalDate.of(2026, 5, 1),
            LocalDate.of(2026, 5, 4),
            BigDecimal.valueOf(10),
            BigDecimal.valueOf(2),
            BigDecimal.valueOf(1),
            BigDecimal.valueOf(1),
            0,
            0);

    // 3 days * 10 sqm * 2/day = 60.00
    assertThat(total).isEqualByComparingTo("60.00");
  }

  @Test
  @DisplayName("uses the weekly rate for bookings between 7 and 29 days")
  void usesWeeklyRate_forMidLengthBookings() {
    BigDecimal total =
        service.calculateTotalPrice(
            LocalDate.of(2026, 5, 1),
            LocalDate.of(2026, 5, 11),
            BigDecimal.valueOf(5),
            BigDecimal.valueOf(10),
            BigDecimal.valueOf(7),
            BigDecimal.valueOf(5),
            0,
            0);

    // 10 days * 5 sqm * 7/day = 350.00 (weekly rate kicks in at 7 days)
    assertThat(total).isEqualByComparingTo("350.00");
  }

  @Test
  @DisplayName("uses the monthly rate for bookings of 30 days or more")
  void usesMonthlyRate_forLongBookings() {
    BigDecimal total =
        service.calculateTotalPrice(
            LocalDate.of(2026, 5, 1),
            LocalDate.of(2026, 6, 1),
            BigDecimal.valueOf(20),
            BigDecimal.valueOf(10),
            BigDecimal.valueOf(7),
            BigDecimal.valueOf(5),
            0,
            0);

    // 31 days * 20 sqm * 5/day = 3100.00 (monthly rate)
    assertThat(total).isEqualByComparingTo("3100.00");
  }

  @Test
  @DisplayName("applies zone discount and warehouse discount sequentially")
  void appliesDiscountsSequentially() {
    BigDecimal total =
        service.calculateTotalPrice(
            LocalDate.of(2026, 5, 1),
            LocalDate.of(2026, 5, 4),
            BigDecimal.valueOf(10),
            BigDecimal.valueOf(2),
            BigDecimal.valueOf(1),
            BigDecimal.valueOf(1),
            10,
            20);

    // base = 60.00 -> -10% zone = 54.00 -> -20% warehouse = 43.20
    assertThat(total).isEqualByComparingTo("43.20");
  }
}
