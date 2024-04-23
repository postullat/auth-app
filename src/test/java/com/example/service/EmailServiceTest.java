package com.example.service;

import com.example.entity.User;
import com.example.exceptions.EmailAlreadyVerifiedException;
import com.example.exceptions.ResetPasswordEmailSendingException;
import com.example.repository.UserRepository;
import com.example.utils.JwtTokenUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private UserDetailsServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenUtils jwtTokenUtils;

    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        emailService = new EmailService(mailSender, userService, userRepository, jwtTokenUtils);
    }

    @Test
    void sendResetPasswordEmail_Success() {
        String email = "test@example.com";
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        assertDoesNotThrow(() -> emailService.sendResetPasswordEmail(email));
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendResetPasswordEmail_Fail() {
        String email = "test@example.com";
        doThrow(new MailSendException("")).when(mailSender).send(any(SimpleMailMessage.class));

        assertThrows(ResetPasswordEmailSendingException.class, () -> emailService.sendResetPasswordEmail(email));
    }

    @Test
    void resendEmailConfirm_Success() {
        String email = "test@example.com";
        User user = new User();
        user.setEmailVerified(false);

        when(userService.findByEmail(anyString())).thenReturn(java.util.Optional.of(user));
        when(userService.loadUserByUsername(anyString())).thenReturn(null);
        when(jwtTokenUtils.generateToken(any())).thenReturn("test_token");
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        assertDoesNotThrow(() -> emailService.resendEmailConfirm(email));
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void resendEmailConfirm_EmailAlreadyVerified() {
        String email = "test@example.com";
        User user = new User();
        user.setEmailVerified(true);

        when(userService.findByEmail(anyString())).thenReturn(java.util.Optional.of(user));

        assertThrows(EmailAlreadyVerifiedException.class, () -> emailService.resendEmailConfirm(email));
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void resendEmailConfirm_UserNotFound() {
        String email = "test@example.com";

        when(userService.findByEmail(anyString())).thenReturn(java.util.Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> emailService.resendEmailConfirm(email));
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }
}
