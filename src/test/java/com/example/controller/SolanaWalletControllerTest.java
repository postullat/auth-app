package com.example.controller;

import com.example.payload.solana.Balance;
import com.example.payload.solana.SolanaWallet;
import com.example.payload.solana.TransferResult;
import com.example.service.SolanaWalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigInteger;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class SolanaWalletControllerTest {

    @Mock
    private SolanaWalletService walletService;

    @InjectMocks
    private SolanaWalletController walletController;

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
    @DisplayName("Given generate wallet request, when generateWallet is called, then returns OK status")
    public void givenGenerateWalletRequest_whenGenerateWalletIsCalled_thenReturnsOkStatus() {
        // Given
        SolanaWallet wallet = new SolanaWallet("publicKey");

        // When
        when(walletService.generateWalletAddressReceiver()).thenReturn(wallet);
        ResponseEntity<SolanaWallet> responseEntity = walletController.generateWallet();

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(wallet, responseEntity.getBody());
        verify(walletService, times(1)).generateWalletAddressReceiver();
    }

    @Test
    @DisplayName("Given get balance request with valid public key, when getBalance is called, then returns OK status")
    public void givenValidPublicKey_whenGetBalanceIsCalled_thenReturnsOkStatus() {
        // Given
        String publicKey = "validPublicKey";
        BigInteger balance = BigInteger.valueOf(1000L);

        // When
        when(walletService.getBalance(publicKey)).thenReturn(new Balance(balance));
        ResponseEntity<?> responseEntity = walletController.getBalance(publicKey);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(balance, ((Balance) Objects.requireNonNull(responseEntity.getBody())).balance());
        verify(walletService, times(1)).getBalance(publicKey);
    }


    @Test
    @DisplayName("Given get balance request with invalid public key, when getBalance is called, then returns INTERNAL_SERVER_ERROR status")
    public void givenInvalidPublicKey_whenGetBalanceIsCalled_thenReturnsInternalServerErrorStatus() {
        // Given
        String publicKey = "invalidPublicKey";

        // When
        when(walletService.getBalance(publicKey)).thenThrow(new RuntimeException("Invalid public key"));
        ResponseEntity<?> responseEntity = walletController.getBalance(publicKey);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        verify(walletService, times(1)).getBalance(publicKey);
    }

    @Test
    @DisplayName("Given transfer funds request, when transferFunds is called, then returns OK status")
    public void givenTransferFundsRequest_whenTransferFundsIsCalled_thenReturnsOkStatus() {
        // Given
        String receiverPublicKey = "receiverPublicKey";
        long lamports = 500L;
        String expectedTransaction = "trdctfvgyuiucbvdyuf";
        TransferResult transferResult = new TransferResult(expectedTransaction);

        // When
        when(walletService.transferFunds(receiverPublicKey, lamports)).thenReturn(transferResult);
        ResponseEntity<?> responseEntity = walletController.transferFunds(receiverPublicKey, lamports);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedTransaction, ((TransferResult) Objects.requireNonNull(responseEntity.getBody())).transaction());
        verify(walletService, times(1)).transferFunds(receiverPublicKey, lamports);
    }



}
