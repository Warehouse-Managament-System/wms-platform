package com.wms.common.exception;

/**
 * Exception thrown when a resource conflict occurs (e.g., duplicate resource, concurrent
 * modification).
 */
public class ResourceConflictException extends WmsException {
  private static final String ERROR_CODE = "RESOURCE_CONFLICT";
  private static final int HTTP_STATUS = 409;

  /**
   * Constructs a ResourceConflictException with the specified message.
   *
   * @param message the detail message
   */
  public ResourceConflictException(String message) {
    super(ERROR_CODE, HTTP_STATUS, message);
  }

  /**
   * Constructs a ResourceConflictException with the specified message and cause.
   *
   * @param message the detail message
   * @param cause the cause of this exception
   */
  public ResourceConflictException(String message, Throwable cause) {
    super(ERROR_CODE, HTTP_STATUS, message, cause);
  }
}
