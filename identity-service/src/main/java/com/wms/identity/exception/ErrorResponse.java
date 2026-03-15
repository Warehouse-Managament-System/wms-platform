package com.wms.identity.exception;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

  private int status;
  private String message;
  private Instant timestamp;
}
