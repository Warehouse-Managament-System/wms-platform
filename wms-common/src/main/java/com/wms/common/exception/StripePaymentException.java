package com.wms.common.exception;

/** Exception thrown when a Stripe payment operation fails. */
public class StripePaymentException extends WmsException {
  private static final String ERROR_CODE = "STRIPE_PAYMENT_ERROR";
  private static final int HTTP_STATUS = 502;

  /**
   * Constructs a StripePaymentException with the specified message.
   *
   * @param message the detail message
   */
  public StripePaymentException(String message) {
    super(ERROR_CODE, HTTP_STATUS, message);
  }

  /**
   * Constructs a StripePaymentException with the specified message and cause.
   *
   * @param message the detail message
   * @param cause the cause of this exception
   */
  public StripePaymentException(String message, Throwable cause) {
    super(ERROR_CODE, HTTP_STATUS, message, cause);
  }
}
