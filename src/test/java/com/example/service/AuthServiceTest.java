package com.example.service;

import com.example.payload.request.ChangePasswordRequest;
import com.example.payload.request.LoginRequest;
import com.example.payload.request.SignupRequest;
import com.example.payload.response.LoginResponse;
import com.example.payload.response.SignupResponse;
import com.example.entity.User;
import com.example.exceptions.EmailNotVerifiedException;
import com.example.exceptions.InvalidPasswordException;
import com.example.exceptions.UserAlreadyExistsException;
import com.example.repository.UserRepository;
import com.example.utils.JwtTokenUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDetailsServiceImpl userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenUtils jwtTokenUtils;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void signup_Success() {
        SignupRequest signupRequest = new SignupRequest("example@example.com", "password");
        User user = new User();

        when(userService.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userService.createNewUser(any())).thenReturn(user);
        when(jwtTokenUtils.generateToken(any())).thenReturn("test_token");
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encodedPassword"); // додати цей рядок

        SignupResponse response = authService.signup(signupRequest);

        assertNotNull(response);
        assertEquals("The user has successfully registered. Please confirm that your email has been sent to your inbox.", response.message());
        verify(emailService, times(1)).sendConfirmationEmail(eq(signupRequest.email()), any());
    }


    @Test
    void signup_Failure_UserAlreadyExists() {
        SignupRequest signupRequest = new SignupRequest("existing@example.com", "password");
        when(userService.findByEmail(signupRequest.email())).thenReturn(java.util.Optional.of(new User()));
        assertThrows(UserAlreadyExistsException.class, () -> authService.signup(signupRequest));
        verifyNoInteractions(emailService);
    }

    @Test
    void login_Success() {
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password");
        when(userService.isEmailVerified(loginRequest.email())).thenReturn(true);
        when(jwtTokenUtils.generateToken(any())).thenReturn("test_token");
        when(jwtTokenUtils.generateRefreshToken(any())).thenReturn("test_refresh_token");
        LoginResponse response = authService.login(loginRequest);
        assertNotNull(response);
        assertEquals("test@example.com", response.email());
        assertNotNull(response.token());
        assertNotNull(response.refreshToken());
    }

    @Test
    void login_Failure_EmailNotVerified() {
        LoginRequest loginRequest = new LoginRequest("unverified@example.com", "password");
        when(userService.isEmailVerified(loginRequest.email())).thenReturn(false);
        assertThrows(EmailNotVerifiedException.class, () -> authService.login(loginRequest));
    }

    @Test
    void login_Failure_InvalidPassword() {
        LoginRequest loginRequest = new LoginRequest("test@example.com", "invalid_password");
        when(userService.isEmailVerified(loginRequest.email())).thenReturn(true);
        when(authenticationManager.authenticate(any())).thenThrow(new InvalidPasswordException("Invalid password provided."));
        assertThrows(InvalidPasswordException.class, () -> authService.login(loginRequest));
    }

    @Test
    void changePassword_Success() {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("token", "new_password");
        User user = new User();
        user.setEmail("test@example.com");
        when(jwtTokenUtils.getUsername(changePasswordRequest.verificationToken())).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(java.util.Optional.of(user));
        authService.changePassword(changePasswordRequest);
        verify(userRepository, times(1)).save(user);
        verify(emailService, times(1)).sendPasswordChangedEmail(user.getEmail());
    }
}
