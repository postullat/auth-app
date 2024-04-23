package com.example.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @Schema(description = "email", example = "user@gmail.com")
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Invalid email format")
        String email,

        @Schema(description = "password", example = "1234qwerty")
        @NotBlank(message = "Password cannot be blank")
        @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
        String password)  {

}