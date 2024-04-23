package com.example.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @Schema(description = "Verification token")
        @NotBlank(message = "VerificationToken cannot be blank")
        String verificationToken,

        @Schema(description = "New password", example = "12345qwerty")
        @NotBlank(message = "Field newPassword cannot be blank")
        @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
        String newPassword) {

}