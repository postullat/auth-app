package com.example.service;

import com.example.payload.solana.Balance;
import com.example.payload.solana.SolanaWallet;
import com.example.payload.solana.TransferResult;
import com.example.exceptions.CustomTransferException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sol4k.*;
import org.sol4k.exception.RpcException;
import org.sol4k.instruction.TransferInstruction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class SolanaWalletService {

    @Value("${solana.sender.secret}")
    private String senderSecret;

    public SolanaWallet generateWalletAddressReceiver() {
        log.info("Generating wallet address receiver");
        var keypair = Keypair.generate();
        var publicAddress = keypair.getPublicKey().toBase58();
        return new SolanaWallet(publicAddress);
    }

    public Balance getBalance(String publicKey) {
        log.info("Getting balance for publicKey: {}", publicKey);
        Connection connection = getConnection();
        BigInteger balance = connection.getBalance(new PublicKey(publicKey));
        return new Balance(balance);
    }

    public TransferResult transferFunds(String receiverPublicKey, long lamports) {
        try {
            log.info("Transferring funds to receiverPublicKey: {} with amount: {}", receiverPublicKey, lamports);
            var connection = getConnection();
            var blockhash = connection.getLatestBlockhash();
            var sender = Keypair.fromSecretKey(Base58.decode(senderSecret));
            var receiver = new PublicKey(receiverPublicKey);
            var instruction = new TransferInstruction(sender.getPublicKey(), receiver, lamports);
            var transaction = new Transaction(blockhash, instruction, sender.getPublicKey());
            transaction.sign(sender);
            var result = connection.sendTransaction(transaction);
            return new TransferResult(result);
        } catch (RpcException e) {
            log.error("Error transferring funds: {}", e.getMessage());
            throw new CustomTransferException("Failed to transfer funds to this wallet: " + receiverPublicKey + " due to an error: " + e.getMessage());
        }

    }

    private Connection getConnection() {
        return new Connection(RpcUrl.DEVNET);
    }

}