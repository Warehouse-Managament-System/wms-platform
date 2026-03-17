package com.wms.identity.config;

import com.wms.common.exception.CommonErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(1)
public class AuthExceptionHandler {

  @ExceptionHandler(DisabledException.class)
  public ResponseEntity<CommonErrorResponse> handleDisabled(
      DisabledException ex, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(
            new CommonErrorResponse(
                Instant.now(),
                HttpStatus.FORBIDDEN.value(),
                "ACCOUNT_DISABLED",
                "Account is not active. Please wait for approval.",
                sanitize(request.getRequestURI())));
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<CommonErrorResponse> handleBadCredentials(
      BadCredentialsException ex, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(
            new CommonErrorResponse(
                Instant.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "BAD_CREDENTIALS",
                "Invalid email or password.",
                sanitize(request.getRequestURI())));
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<CommonErrorResponse> handleAuth(
      AuthenticationException ex, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(
            new CommonErrorResponse(
                Instant.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "AUTHENTICATION_FAILED",
                "Authentication failed.",
                sanitize(request.getRequestURI())));
  }

  private static String sanitize(String input) {
    if (input == null) {
      return null;
    }
    return input
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#x27;");
  }
}
