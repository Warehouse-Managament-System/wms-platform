package com.wms.common.exception;

/** Exception thrown when a resource is unavailable. */
public class AvailabilityException extends WmsException {
  private static final String ERROR_CODE = "RESOURCE_UNAVAILABLE";
  private static final int HTTP_STATUS = 409;

  /**
   * Constructs an AvailabilityException with the specified message.
   *
   * @param message the detail message
   */
  public AvailabilityException(String message) {
    super(ERROR_CODE, HTTP_STATUS, message);
  }

  /**
   * Constructs an AvailabilityException with the specified message and cause.
   *
   * @param message the detail message
   * @param cause the cause of this exception
   */
  public AvailabilityException(String message, Throwable cause) {
    super(ERROR_CODE, HTTP_STATUS, message, cause);
  }
}
