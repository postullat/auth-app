package com.example.controller;

import com.example.entity.User;
import com.example.service.UserDetailsServiceImpl;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@OpenAPIDefinition(
        info = @Info(
                title = "User controller.",
                version = "1.0",
                description = "Controller that allow user to see profile and admin to see all users."
        )
)
public class UserController {

    private final UserDetailsServiceImpl userService;

    @GetMapping("/current-user")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get user information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User information is found.", content = { @Content(mediaType = "text/plain")}),
            @ApiResponse(responseCode = "404", description = "User is not found.", content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))})
    })
    public ResponseEntity<?> getCurrentUser(Principal principal) {
        log.debug("Request for current user details");
        return ResponseEntity.ok(principal);
    }


    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get all user information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users are found.", content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "404", description = "Users are not found.", content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))})
    })
    public ResponseEntity<Page<User>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        log.debug("Request for all users");
        Page<User> users = userService.findAllUsers(page, size);
        return ResponseEntity.ok(users);
    }
}
