package com.wms.common.exception;

/**
 * Base exception class for WMS platform. All custom exceptions in the WMS platform should extend
 * this class.
 */
public abstract class WmsException extends RuntimeException {
  private final String errorCode;
  private final int httpStatus;

  /**
   * Constructs a WmsException with the specified error code, HTTP status, and message.
   *
   * @param errorCode the error code for this exception
   * @param httpStatus the HTTP status code
   * @param message the detail message
   */
  public WmsException(String errorCode, int httpStatus, String message) {
    super(message);
    this.errorCode = errorCode;
    this.httpStatus = httpStatus;
  }

  /**
   * Constructs a WmsException with the specified error code, HTTP status, message, and cause.
   *
   * @param errorCode the error code for this exception
   * @param httpStatus the HTTP status code
   * @param message the detail message
   * @param cause the cause of this exception
   */
  public WmsException(String errorCode, int httpStatus, String message, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
    this.httpStatus = httpStatus;
  }

  /**
   * Gets the error code.
   *
   * @return the error code
   */
  public String getErrorCode() {
    return errorCode;
  }

  /**
   * Gets the HTTP status code.
   *
   * @return the HTTP status code
   */
  public int getHttpStatus() {
    return httpStatus;
  }
}
