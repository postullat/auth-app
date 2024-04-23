package com.example.service;

import com.example.payload.solana.Balance;
import com.example.payload.solana.SolanaWallet;
import com.example.payload.solana.TransferResult;
import com.example.exceptions.CustomTransferException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SolanaWalletServiceTest {

    @Mock
    private SolanaWalletService solanaWalletService;

    @Test
    void generateWalletAddressReceiver_Success() {
        SolanaWallet expectedWallet = new SolanaWallet("testPublicKey");
        when(solanaWalletService.generateWalletAddressReceiver()).thenReturn(expectedWallet);

        SolanaWallet actualWallet = solanaWalletService.generateWalletAddressReceiver();

        assertNotNull(actualWallet);
        assertEquals(expectedWallet, actualWallet);
    }

    @Test
    void getBalance_Success() {
        String publicKey = "testPublicKey";
        Balance expectedBalance = new Balance(BigInteger.valueOf(100));
        when(solanaWalletService.getBalance(publicKey)).thenReturn(expectedBalance);

        Balance actualBalance = solanaWalletService.getBalance(publicKey);

        assertNotNull(actualBalance);
        assertEquals(expectedBalance, actualBalance);
    }

    @Test
    void transferFunds_Success() {
        String receiverPublicKey = "receiverPublicKey";
        long lamports = 100;
        TransferResult expectedTransferResult = new TransferResult("testTransaction");
        when(solanaWalletService.transferFunds(receiverPublicKey, lamports)).thenReturn(expectedTransferResult);

        TransferResult actualTransferResult = solanaWalletService.transferFunds(receiverPublicKey, lamports);

        assertNotNull(actualTransferResult);
        assertEquals(expectedTransferResult, actualTransferResult);
    }

    @Test
    void transferFunds_ExceptionThrown() {
        String receiverPublicKey = "receiverPublicKey";
        long lamports = 100;
        when(solanaWalletService.transferFunds(receiverPublicKey, lamports)).thenThrow(CustomTransferException.class);

        assertThrows(CustomTransferException.class, () -> solanaWalletService.transferFunds(receiverPublicKey, lamports));
    }
}
