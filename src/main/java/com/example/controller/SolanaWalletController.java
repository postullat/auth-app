package com.example.controller;

import com.example.payload.solana.SolanaWallet;
import com.example.service.SolanaWalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
@Slf4j
@Tag(name = "Solana Wallet", description = "Endpoints for Solana wallet operations")
public class SolanaWalletController {

    private final SolanaWalletService walletService;


    @GetMapping("/create-wallet")
    @Operation(summary = "Generate wallet address for receiver")
    public ResponseEntity<SolanaWallet> generateWallet() {
        var wallet = walletService.generateWalletAddressReceiver();
        log.debug("Generated wallet address for receiver: {}", wallet.publicAddress());
        return new ResponseEntity<>(wallet, HttpStatus.OK);
    }

    @GetMapping("/balance/{publicKey}")
    @Operation(summary = "Get wallet balance")
    public ResponseEntity<?> getBalance(@PathVariable String publicKey) {
        try {
            var balance = walletService.getBalance(publicKey);
            log.debug("Retrieved balance for wallet: {}", publicKey);
            return new ResponseEntity<>(balance, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurred while getting balance for wallet: {}", publicKey, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/transfer/{receiverPublicKey}/{lamports}")
    @Operation(summary = "Transfer funds to wallet")
    public ResponseEntity<?> transferFunds(@PathVariable String receiverPublicKey, @PathVariable long lamports) {
        var result = walletService.transferFunds(receiverPublicKey, lamports);
        log.debug("Transferred {} lamports to wallet: {}", lamports, receiverPublicKey);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}