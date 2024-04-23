package com.example.payload.solana;

import io.swagger.v3.oas.annotations.media.Schema;

public record TransferResult(
        @Schema(description = "Public transaction of operation", example = "2An48FCBzoHrNxakGSE27EatDc1e3EFyTAuhhgYk12AdyNebU42tHcUxDd2ry4ZB3p93b4XKkxw2re4on73cfsoo")
        String transaction) {
}