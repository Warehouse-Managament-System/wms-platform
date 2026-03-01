package com.wms.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for the WMS platform. Handles all WmsException subclasses and other
 * common exceptions, converting them to standardized CommonErrorResponse objects.
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

  /**
   * Handles WmsException and all its subclasses.
   *
   * @param ex the exception that was thrown
   * @param request the HTTP request
   * @return a ResponseEntity with CommonErrorResponse
   */
  @ExceptionHandler(WmsException.class)
  public ResponseEntity<CommonErrorResponse> handleWmsException(
      WmsException ex, HttpServletRequest request) {
    log.error("WMS Exception occurred: {} - {}", ex.getErrorCode(), ex.getMessage(), ex);

    CommonErrorResponse response =
        new CommonErrorResponse(
            Instant.now(),
            ex.getHttpStatus(),
            ex.getErrorCode(),
            ex.getMessage(),
            request.getRequestURI());

    return ResponseEntity.status(ex.getHttpStatus()).body(response);
  }

  /**
   * Handles validation errors from method argument validation.
   *
   * @param ex the exception that was thrown
   * @param request the HTTP request
   * @return a ResponseEntity with CommonErrorResponse
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<CommonErrorResponse> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    String fieldErrors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));

    String message = "Validation failed: " + fieldErrors;

    log.warn("Validation error: {}", message);

    CommonErrorResponse response =
        new CommonErrorResponse(
            Instant.now(),
            HttpStatus.BAD_REQUEST.value(),
            "VALIDATION_ERROR",
            message,
            request.getRequestURI());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  /**
   * Handles constraint violation exceptions from validation.
   *
   * @param ex the exception that was thrown
   * @param request the HTTP request
   * @return a ResponseEntity with CommonErrorResponse
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<CommonErrorResponse> handleConstraintViolation(
      ConstraintViolationException ex, HttpServletRequest request) {
    String violations =
        ex.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining(", "));

    String message = "Constraint violation: " + violations;

    log.warn("Constraint violation: {}", message);

    CommonErrorResponse response =
        new CommonErrorResponse(
            Instant.now(),
            HttpStatus.BAD_REQUEST.value(),
            "VALIDATION_ERROR",
            message,
            request.getRequestURI());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  /**
   * Generic exception handler for any unhandled exceptions.
   *
   * @param ex the exception that was thrown
   * @param request the HTTP request
   * @return a ResponseEntity with CommonErrorResponse
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<CommonErrorResponse> handleGenericException(
      Exception ex, HttpServletRequest request) {
    log.error("Unexpected exception occurred", ex);

    CommonErrorResponse response =
        new CommonErrorResponse(
            Instant.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "INTERNAL_SERVER_ERROR",
            "An unexpected error occurred. Please try again later.",
            request.getRequestURI());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }
}
