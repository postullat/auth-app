package com.example.controller;

import com.example.entity.User;
import com.example.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserDetailsServiceImpl userService;

    @InjectMocks
    private UserController userController;

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
    @DisplayName("Given current user request, when getCurrentUser is called, then returns principal")
    public void givenCurrentUserRequest_whenGetCurrentUserIsCalled_thenReturnsPrincipal() {
        // Given
        Principal principal = mock(Principal.class);

        // When
        ResponseEntity<?> responseEntity = userController.getCurrentUser(principal);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(principal, responseEntity.getBody());
    }

    @Test
    @DisplayName("Given admin user request, when getAllUsers is called, then returns page of users")
    public void givenAdminUserRequest_whenGetAllUsersIsCalled_thenReturnsPageOfUsers() {
        // Given
        int page = 0;
        int size = 10;
        Page<User> users = mock(Page.class);
        when(userService.findAllUsers(page, size)).thenReturn(users);

        // When
        ResponseEntity<Page<User>> responseEntity = userController.getAllUsers(page, size);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(users, responseEntity.getBody());
        verify(userService, times(1)).findAllUsers(page, size);
    }
}
