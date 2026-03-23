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
import org.springframework.web.util.HtmlUtils;

@RestControllerAdvice
@Order(1)
public class AuthExceptionHandler {

  private static String sanitize(String input) {
    if (input == null) return null;
    return HtmlUtils.htmlEscape(input);
  }

  @ExceptionHandler(DisabledException.class)
  public ResponseEntity<CommonErrorResponse> handleDisabled(HttpServletRequest request) {
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
  public ResponseEntity<CommonErrorResponse> handleBadCredentials(HttpServletRequest request) {
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
  public ResponseEntity<CommonErrorResponse> handleAuth(HttpServletRequest request) {

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(
            new CommonErrorResponse(
                Instant.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "AUTHENTICATION_FAILED",
                "Authentication failed.",
                sanitize(request.getRequestURI())));
  }
}
