package com.example.service;


import com.example.exceptions.EmailNotVerifiedException;
import com.example.exceptions.InvalidPasswordException;
import com.example.exceptions.UserAlreadyExistsException;
import com.example.payload.request.ChangePasswordRequest;
import com.example.payload.request.LoginRequest;
import com.example.payload.request.SignupRequest;
import com.example.payload.response.LoginResponse;
import com.example.payload.response.SignupResponse;
import com.example.entity.User;
import com.example.repository.UserRepository;
import com.example.utils.JwtTokenUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final UserDetailsServiceImpl userService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;


    @Transactional
    public SignupResponse signup(SignupRequest request) {
        log.debug("Attempting to sign up user with email: {}", request.email());

        if (userService.findByEmail(request.email()).isEmpty()) {
            User user = userService.createNewUser(request);
            var userDetail = getUserDetails(request.email());
            String token = jwtTokenUtils.generateToken(userDetail);
            emailService.sendConfirmationEmail(request.email(), token);
            userRepository.save(user);
            log.info("User signed up successfully with email: {}", request.email());

            return new SignupResponse("The user has successfully registered. Please confirm that your email has been sent to your inbox.");
        } else {
            log.error("User with email {} already exists", request.email());
            throw new UserAlreadyExistsException("User with email: " + request.email() + " already exists");
        }
    }

    public LoginResponse login(LoginRequest request) {

        log.debug("Attempting to login user with email: {}", request.email());
        try {
            if (userService.isEmailVerified(request.email())) {
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
                String token = jwtTokenUtils.generateToken(getUserDetails(request.email()));
                String refreshToken = jwtTokenUtils.generateRefreshToken(getUserDetails(request.email()));

                log.info("User logged in successfully with email: {}", request.email());
                return new LoginResponse(request.email(), token, refreshToken);
            } else {
                throw new EmailNotVerifiedException("Email: " + request.email() + " not verified.");
            }
        } catch (BadCredentialsException ex) {
            log.error("Invalid password provided for email: {}", request.email());
            throw new InvalidPasswordException("Invalid password provided.");
        }
    }


    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        String email = jwtTokenUtils.getUsername(request.verificationToken());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        String newPasswordEncoded = passwordEncoder.encode(request.newPassword());
        user.setPassword(newPasswordEncoded);

        userRepository.save(user);
        emailService.sendPasswordChangedEmail(email);

        log.info("Password changed successfully for user: {}", email);
    }


    private UserDetails getUserDetails(String email) {
        return userService.loadUserByUsername(email);
    }

}
