package com.wms.common.exception;

/** Exception thrown when a business rule is violated. */
public class BusinessRuleException extends WmsException {
  private static final String ERROR_CODE = "BUSINESS_RULE_VIOLATION";
  private static final int HTTP_STATUS = 422;

  /**
   * Constructs a BusinessRuleException with the specified message.
   *
   * @param message the detail message
   */
  public BusinessRuleException(String message) {
    super(ERROR_CODE, HTTP_STATUS, message);
  }

  /**
   * Constructs a BusinessRuleException with the specified message and cause.
   *
   * @param message the detail message
   * @param cause the cause of this exception
   */
  public BusinessRuleException(String message, Throwable cause) {
    super(ERROR_CODE, HTTP_STATUS, message, cause);
  }
}
