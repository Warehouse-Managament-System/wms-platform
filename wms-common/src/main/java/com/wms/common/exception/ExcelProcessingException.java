package com.wms.common.exception;

/** Exception thrown when Excel file processing fails. */
public class ExcelProcessingException extends WmsException {
  private static final String ERROR_CODE = "EXCEL_PROCESSING_ERROR";
  private static final int HTTP_STATUS = 422;

  /**
   * Constructs an ExcelProcessingException with the specified message.
   *
   * @param message the detail message
   */
  public ExcelProcessingException(String message) {
    super(ERROR_CODE, HTTP_STATUS, message);
  }

  /**
   * Constructs an ExcelProcessingException with the specified message and cause.
   *
   * @param message the detail message
   * @param cause the cause of this exception
   */
  public ExcelProcessingException(String message, Throwable cause) {
    super(ERROR_CODE, HTTP_STATUS, message, cause);
  }
}
