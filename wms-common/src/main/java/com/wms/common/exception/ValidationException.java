package com.wms.common.exception;

/** Exception thrown when validation fails. */
public class ValidationException extends WmsException {
  private static final String ERROR_CODE = "VALIDATION_ERROR";
  private static final int HTTP_STATUS = 400;

  /**
   * Constructs a ValidationException with the specified message.
   *
   * @param message the detail message
   */
  public ValidationException(String message) {
    super(ERROR_CODE, HTTP_STATUS, message);
  }

  /**
   * Constructs a ValidationException with the specified message and cause.
   *
   * @param message the detail message
   * @param cause the cause of this exception
   */
  public ValidationException(String message, Throwable cause) {
    super(ERROR_CODE, HTTP_STATUS, message, cause);
  }
}
