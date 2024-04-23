package com.example.payload.solana;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigInteger;

public record Balance(
        @Schema(description = "Balance of wallet.", example = "0")
        BigInteger balance) {
}
