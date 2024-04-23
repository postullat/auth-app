package com.example.controller;

import com.example.exceptions.ErrorResponse;
import com.example.payload.request.ChangePasswordRequest;
import com.example.payload.request.LoginRequest;
import com.example.payload.request.SignupRequest;
import com.example.payload.response.LoginResponse;
import com.example.service.AuthService;
import com.example.service.EmailService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
@OpenAPIDefinition(
        info = @Info(
                title = "Authentication controller.",
                version = "1.0",
                description = "Controller that allow user to signup, login, logout, confirm email, change password."
        )
)
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;

    @PostMapping("/signup")
    @Operation(summary = "Signup.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User is created but email is not confirmed. User should check him email to confirm it.", content = { @Content(mediaType = "text/plain",
                    schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "208", description = "User is already exists.", content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))})
    })
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest requestDto) {
        var response = authService.signup(requestDto);
        log.debug("New user signed up: {}", requestDto.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Login.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User is authenticated.", content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = LoginResponse.class))}),
            @ApiResponse(responseCode = "404", description = "User is not found.", content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Your email is not confirmed.", content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Bad credentials.", content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))})
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        log.debug("User logged in: {}", request.email());
        return ResponseEntity.ok(response);
    }


    @GetMapping("/resend/email-confirmation/{email}")
    @Operation(summary = "Request for email confirmation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Letter is sent.", content = { @Content(mediaType = "text/plain")}),
            @ApiResponse(responseCode = "404", description = "User is not found.", content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Token is not found.", content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))})
    })
    public ResponseEntity<?> resendEmailConfirmation(@PathVariable String email) {
        emailService.resendEmailConfirm(email);
        log.debug("Email confirmation resent for email: {}", email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/email-confirm/{token}")
    @Operation(summary = "Email confirmation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thanks for your confirmation.", content = { @Content(mediaType = "text/plain")}),
            @ApiResponse(responseCode = "500", description = "Oops something went wrong, try again...", content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))})
    })
    public ResponseEntity<?> emailConfirmByToken(@PathVariable String token) {
        emailService.confirmEmailByToken(token);
        log.debug("Email confirmed by token: {}", token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/send/reset-password-email/{email}")
    @Operation(summary = "Request for changing password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The letter has been sent.", content = { @Content(mediaType = "text/plain")}),
            @ApiResponse(responseCode = "404", description = "User is not found.", content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))})
    })
    public ResponseEntity<?> resetPasswordByEmail(@PathVariable String email) {
        emailService.sendResetPasswordEmail(email);
        log.debug("Reset password email sent for email: {}", email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "The password is changed!", content = { @Content(mediaType = "text/plain")}),
            @ApiResponse(responseCode = "404", description = "User is not found.", content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Not right token. Try again...", content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))})
    })
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        authService.changePassword(changePasswordRequest);
        log.debug("Password changed for user with token: {}", changePasswordRequest.verificationToken());
        return ResponseEntity.ok().build();
    }
    
}

