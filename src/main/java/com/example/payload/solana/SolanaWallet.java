package com.example.payload.solana;

import io.swagger.v3.oas.annotations.media.Schema;

public record SolanaWallet(
        @Schema(description = "Public address of the wallet", example = "4p5UFW3vskr6923tRiA1kLXrTz3UgRVzZmt1x56xNnYg")
        String publicAddress) {
}