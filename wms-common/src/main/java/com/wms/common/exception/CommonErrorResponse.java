package com.wms.common.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;

public record CommonErrorResponse(
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            timezone = "UTC")
        Instant timestamp,
    int status,
    String errorCode,
    String message,
    String path) {}
