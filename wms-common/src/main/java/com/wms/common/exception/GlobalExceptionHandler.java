package com.wms.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.HtmlUtils;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

  @ExceptionHandler(WmsException.class)
  public ResponseEntity<CommonErrorResponse> handleWmsException(
      WmsException ex, HttpServletRequest request) {
    log.error("WMS Exception occurred: {} - {}", ex.getErrorCode(), ex.getMessage(), ex);

    CommonErrorResponse response =
        new CommonErrorResponse(
            Instant.now(),
            ex.getHttpStatus(),
            ex.getErrorCode(),
            sanitize(ex.getMessage()),
            sanitize(request.getRequestURI()));

    return ResponseEntity.status(ex.getHttpStatus()).body(response);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<CommonErrorResponse> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    String fieldErrors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(error -> sanitize(error.getField()) + ": " + sanitize(error.getDefaultMessage()))
            .collect(Collectors.joining(", "));

    String message = "Validation failed: " + fieldErrors;

    log.warn("Validation error: {}", message);

    CommonErrorResponse response =
        new CommonErrorResponse(
            Instant.now(),
            HttpStatus.BAD_REQUEST.value(),
            "VALIDATION_ERROR",
            message,
            sanitize(request.getRequestURI()));

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<CommonErrorResponse> handleConstraintViolation(
      ConstraintViolationException ex, HttpServletRequest request) {
    String violations =
        ex.getConstraintViolations().stream()
            .map(v -> sanitize(v.getMessage()))
            .collect(Collectors.joining(", "));

    String message = "Constraint violation: " + violations;

    log.warn("Constraint violation: {}", message);

    CommonErrorResponse response =
        new CommonErrorResponse(
            Instant.now(),
            HttpStatus.BAD_REQUEST.value(),
            "VALIDATION_ERROR",
            message,
            sanitize(request.getRequestURI()));

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<CommonErrorResponse> handleDataIntegrityViolation(
      DataIntegrityViolationException ex, HttpServletRequest request) {
    log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());

    CommonErrorResponse response =
        new CommonErrorResponse(
            Instant.now(),
            HttpStatus.CONFLICT.value(),
            "RESOURCE_CONFLICT",
            "A conflicting resource already exists.",
            sanitize(request.getRequestURI()));

    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
  }

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
            sanitize(request.getRequestURI()));

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }

  private static String sanitize(String input) {
    if (input == null) {
      return null;
    }
    return HtmlUtils.htmlEscape(input);
  }
}
