package com.example.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenUtilsTest {
    @InjectMocks
    private JwtTokenUtils jwtTokenUtils;

    @BeforeEach
    void setUp() {
        jwtTokenUtils = new JwtTokenUtils();
        jwtTokenUtils.setSecretKey("testSecretKey");
        jwtTokenUtils.setSecretKeyForRefresh("testRefreshSecretKey");
        jwtTokenUtils.setJwtLifetime(Duration.ofMinutes(30));
        jwtTokenUtils.setJwtRefreshLifetime(Duration.ofDays(1));
    }

    @Test
    @DisplayName("Generate Token - Positive Case")
    void generateToken_PositiveCase() {
        UserDetails userDetails = new User("testUser", "password", Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
        String token = jwtTokenUtils.generateToken(userDetails);

        assertNotNull(token);
        assertEquals("testUser", jwtTokenUtils.getUsername(token));
        assertEquals(1, jwtTokenUtils.getRoles(token).size());
        assertEquals("ROLE_USER", jwtTokenUtils.getRoles(token).get(0));
    }

    @Test
    @DisplayName("Generate Refresh Token - Positive Case")
    void generateRefreshToken_PositiveCase() {
        UserDetails userDetails = new User("testUser", "password", Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")));
        jwtTokenUtils.setSecretKeyForRefresh("testSecretKey");
        String refreshToken = jwtTokenUtils.generateRefreshToken(userDetails);

        assertNotNull(refreshToken);
        assertEquals("testUser", jwtTokenUtils.getUsername(refreshToken));
        assertEquals(1, jwtTokenUtils.getRoles(refreshToken).size());
        assertEquals("ROLE_ADMIN", jwtTokenUtils.getRoles(refreshToken).get(0));
    }


    @Test
    @DisplayName("Generate Token - Null UserDetails")
    void generateToken_NullUserDetails() {
        assertThrows(NullPointerException.class, () -> jwtTokenUtils.generateToken(null));
    }

    @Test
    @DisplayName("Generate Refresh Token - Null UserDetails")
    void generateRefreshToken_NullUserDetails() {
        assertThrows(NullPointerException.class, () -> jwtTokenUtils.generateRefreshToken(null));
    }

    @Test
    @DisplayName("Get Username - Null Token")
    void getUsername_NullToken() {
        assertThrows(IllegalArgumentException.class, () -> jwtTokenUtils.getUsername(null));
    }

    @Test
    @DisplayName("Get Roles - Null Token")
    void getRoles_NullToken() {
        assertThrows(IllegalArgumentException.class, () -> jwtTokenUtils.getRoles(null));
    }

    @Test
    @DisplayName("Get Roles - Invalid Token")
    void getRoles_InvalidToken() {
        assertThrows(Exception.class, () -> jwtTokenUtils.getRoles("invalidToken"));
    }
}
