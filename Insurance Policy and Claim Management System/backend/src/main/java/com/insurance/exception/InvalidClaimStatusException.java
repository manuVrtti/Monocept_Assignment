package com.insurance.exception;

public class InvalidClaimStatusException extends RuntimeException {
    public InvalidClaimStatusException(String message) {
        super(message);
    }
}
