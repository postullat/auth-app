package com.example.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record SignupResponse(
        @Schema(description = "Notification of successful registration.")
        String message) {

}
