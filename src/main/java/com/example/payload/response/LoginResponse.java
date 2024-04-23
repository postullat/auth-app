package com.example.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(
        @Schema(description = "email")
        String email,
        @Schema(description = "JWT token")
        String token,

        @Schema(description = "Refresh JWT token")
        String refreshToken) {

}