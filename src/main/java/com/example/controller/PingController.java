package com.example.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ping")
@OpenAPIDefinition(
        info = @Info(
                title = "Default controller.",
                version = "1.0",
                description = "Controller that allow user to check if the app is running."
        )
)
@Log4j2
public class PingController {
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Ping application.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The app is running.", content = { @Content(mediaType = "text/plain")})
    })
    public String ping(){
        String message = "The app is running";
        log.info(message);
        return message;
    }
}
