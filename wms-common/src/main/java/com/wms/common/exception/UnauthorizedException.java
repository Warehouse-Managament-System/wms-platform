package com.wms.common.exception;

/** Exception thrown when a user is not authorized to perform an action. */
public class UnauthorizedException extends WmsException {
  private static final String ERROR_CODE = "UNAUTHORIZED";
  private static final int HTTP_STATUS = 403;

  /**
   * Constructs an UnauthorizedException with the specified message.
   *
   * @param message the detail message
   */
  public UnauthorizedException(String message) {
    super(ERROR_CODE, HTTP_STATUS, message);
  }

  /**
   * Constructs an UnauthorizedException with the specified message and cause.
   *
   * @param message the detail message
   * @param cause the cause of this exception
   */
  public UnauthorizedException(String message, Throwable cause) {
    super(ERROR_CODE, HTTP_STATUS, message, cause);
  }
}
