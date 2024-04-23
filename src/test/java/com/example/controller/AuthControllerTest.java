package com.example.controller;

import com.example.payload.request.ChangePasswordRequest;
import com.example.payload.request.LoginRequest;
import com.example.payload.request.SignupRequest;
import com.example.payload.response.LoginResponse;
import com.example.service.AuthService;
import com.example.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthController authController;

    @InjectMocks
    private PingController pingController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Given ping request, when ping is called, then returns pong")
    public void givenPingRequest_whenPingIsCalled_thenReturnsPong() {
        // When
        String result = pingController.ping();

        // Then
        assertEquals("pong", result);
    }

    @Test
    @DisplayName("Given valid signup request, when signup is called, then returns CREATED status")
    public void givenValidSignupRequest_whenSignupIsCalled_thenReturnsCreatedStatus() {
        // Given
        SignupRequest signupRequest = new SignupRequest("user@gmail.com", "1234567");

        // When
        ResponseEntity<?> responseEntity = authController.signup(signupRequest);

        // Then
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        verify(authService, times(1)).signup(signupRequest);
    }

    @Test
    @DisplayName("Given valid login request, when login is called, then returns OK status")
    public void givenValidLoginRequest_whenLoginIsCalled_thenReturnsOkStatus() {
        // Given
        LoginRequest loginRequest = new LoginRequest("user@gmail.com", "1234567");

        // When
        ResponseEntity<LoginResponse> responseEntity = authController.login(loginRequest);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(authService, times(1)).login(loginRequest);
    }

    @Test
    @DisplayName("Given valid email, when resendEmailConfirmation is called, then returns OK status")
    public void givenValidEmail_whenResendEmailConfirmationIsCalled_thenReturnsOkStatus() {
        // Given
        String email = "user@gmail.com";

        // When
        ResponseEntity<?> responseEntity = authController.resendEmailConfirmation(email);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(emailService, times(1)).resendEmailConfirm(email);
    }

    @Test
    @DisplayName("Given valid token, when emailConfirmByToken is called, then returns OK status")
    public void givenValidToken_whenEmailConfirmByTokenIsCalled_thenReturnsOkStatus() {
        // Given
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGdtYWlsLmNvbSIsInJvbGVzIjpbIlJPTEVfVVNFUiJdLCJleHAiOjE3MTUyMDYyODUsImlhdCI6MTcxMjYxNDI4NX0.fTMuT73568nm9OZdjYuHahEozjZWT-le8EEBgJq4TVQ";

        // When
        ResponseEntity<?> responseEntity = authController.emailConfirmByToken(token);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(emailService, times(1)).confirmEmailByToken(token);
    }

    @Test
    @DisplayName("Given valid email, when resetPasswordByEmail is called, then returns OK status")
    public void givenValidEmail_whenResetPasswordByEmailIsCalled_thenReturnsOkStatus() {
        // Given
        String email = "user@gmail.com";

        // When
        ResponseEntity<?> responseEntity = authController.resetPasswordByEmail(email);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(emailService, times(1)).sendResetPasswordEmail(email);
    }

    @Test
    @DisplayName("Given valid change password request, when changePassword is called, then returns OK status")
    public void givenValidChangePasswordRequest_whenChangePasswordIsCalled_thenReturnsOkStatus() {
        // Given
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGdtYWlsLmNvbSIsInJvbGVzIjpbIlJPTEVfVVNFUiJdLCJleHAiOjE3MTUyMDYyODUsImlhdCI6MTcxMjYxNDI4NX0.fTMuT73568nm9OZdjYuHahEozjZWT-le8EEBgJq4TVQ", "new_password");

        // When
        ResponseEntity<?> responseEntity = authController.changePassword(changePasswordRequest);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(authService, times(1)).changePassword(changePasswordRequest);
    }
}
