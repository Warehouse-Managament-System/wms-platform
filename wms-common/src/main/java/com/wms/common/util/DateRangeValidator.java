package com.wms.common.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.Instant;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {

  private String startField;
  private String endField;

  @Override
  public void initialize(ValidDateRange constraintAnnotation) {
    startField = constraintAnnotation.startField();
    endField = constraintAnnotation.endField();
  }

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }

    BeanWrapper wrapper = new BeanWrapperImpl(value);
    Instant startDate = (Instant) wrapper.getPropertyValue(startField);
    Instant endDate = (Instant) wrapper.getPropertyValue(endField);

    if (startDate == null || endDate == null) {
      return true;
    }

    return startDate.isBefore(endDate);
  }
}
