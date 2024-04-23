package com.example.service;

import com.example.payload.request.SignupRequest;
import com.example.entity.Role;
import com.example.entity.User;
import com.example.repository.RoleRepository;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;


public class UserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDetailsService = new UserDetailsServiceImpl(userRepository, roleRepository, passwordEncoder);
    }

    @Test
    void loadUserByUsername_UserFound() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        Role role = new Role();
        role.setName("ROLE_USER");
        user.setRoles(List.of(role));

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("test@example.com"));
    }

    @Test
    void createNewUser_Success() {
        SignupRequest signupRequest = new SignupRequest("test@example.com", "password");
        Role role = new Role();
        role.setName("ROLE_USER");

        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setEmail("test@example.com");
        savedUser.setPassword("encodedPassword");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User newUser = userDetailsService.createNewUser(signupRequest);

        assertNotNull(newUser);
        assertEquals("test@example.com", newUser.getEmail());
        assertEquals("encodedPassword", newUser.getPassword());
    }

    @Test
    void isEmailVerified_Verified() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setEmailVerified(true);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        assertTrue(userDetailsService.isEmailVerified("test@example.com"));
    }

    @Test
    void isEmailVerified_NotVerified() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setEmailVerified(false);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        assertFalse(userDetailsService.isEmailVerified("test@example.com"));
    }
}
