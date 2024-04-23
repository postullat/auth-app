package com.example.service;

import com.example.entity.User;
import com.example.exceptions.EmailAlreadyVerifiedException;
import com.example.exceptions.PasswordChangeEmailSendingException;
import com.example.exceptions.ResetPasswordEmailSendingException;
import com.example.repository.UserRepository;
import com.example.utils.JwtTokenUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final UserDetailsServiceImpl userService;
    private final UserRepository userRepository;
    private final JwtTokenUtils jwtTokenUtils;

    @Value("${email.sender}")
    private String emailSender;

    @Value("${email.subject}")
    private String emailSubject;

    @Value("${email.confirmation.url}")
    private String emailConfirmationUrl;

    @Value("${email.reset-password.url}")
    private String resetPasswordUrl;

    public void sendConfirmationEmail(String email, String token) {
        SimpleMailMessage message = createSimpleMailMessage(email, emailConfirmationUrl + token, emailSubject);
        mailSender.send(message);
        log.info("Confirmation email sent to: {}", email);
    }

    public void confirmEmailByToken(String token) {
        String email = jwtTokenUtils.getUsername(token);
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

        user.setEmailVerified(true);
        userRepository.save(user);
        log.info("Email confirmed for user: {}", email);

    }

    public void resendEmailConfirm(String email) {
        var user = userService.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        if (user.isEmailVerified()) {
            throw new EmailAlreadyVerifiedException("Email already verified for user: " + email);
        }
        UserDetails userDetails = userService.loadUserByUsername(email);
        var token = jwtTokenUtils.generateToken(userDetails);
        sendConfirmationEmail(email, token);
        log.info("Resent email confirmation for: {}", email);
    }

    public void sendResetPasswordEmail(String email) {
        try {
            SimpleMailMessage message = createSimpleMailMessage(email, resetPasswordUrl, "Reset Your Password");
            mailSender.send(message);
            log.info("Reset password email sent to: {}", email);
        } catch (MailException e) {
            throw new ResetPasswordEmailSendingException("Failed to send reset password email");
        }
    }

    public void sendPasswordChangedEmail(String email) {
        try {
            SimpleMailMessage message = createSimpleMailMessage(email, "Your password has been changed successfully.", "Password Changed");
            mailSender.send(message);
            log.info("Password change email sent to: {}", email);
        } catch (MailException e) {
            throw new PasswordChangeEmailSendingException("Failed to send password change email");
        }
    }


    private SimpleMailMessage createSimpleMailMessage(String to, String text, String subject) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailSender);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        return message;
    }


}
