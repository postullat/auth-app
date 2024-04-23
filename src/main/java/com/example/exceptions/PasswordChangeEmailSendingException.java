package com.example.exceptions;

public class PasswordChangeEmailSendingException extends RuntimeException {

    public PasswordChangeEmailSendingException(String message) {
        super(message);
    }
}
