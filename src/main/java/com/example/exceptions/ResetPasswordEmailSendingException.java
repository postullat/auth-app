package com.example.exceptions;

public class ResetPasswordEmailSendingException extends RuntimeException {
    public ResetPasswordEmailSendingException(String message) {
        super(message);
    }
}