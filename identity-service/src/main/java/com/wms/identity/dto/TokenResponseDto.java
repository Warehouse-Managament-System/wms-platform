package com.wms.identity.dto;

public record TokenResponseDto(
    String accessToken,
    String refreshToken
) {

}

