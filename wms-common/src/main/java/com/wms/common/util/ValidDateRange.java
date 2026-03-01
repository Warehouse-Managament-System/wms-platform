package com.wms.common.util;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateRangeValidator.class)
public @interface ValidDateRange {
  String message() default "Start date must be before end date";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String startField() default "startDate";

  String endField() default "endDate";
}
