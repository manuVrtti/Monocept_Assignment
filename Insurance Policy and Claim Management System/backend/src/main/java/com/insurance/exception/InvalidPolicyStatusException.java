package com.insurance.exception;

public class InvalidPolicyStatusException extends RuntimeException {
    public InvalidPolicyStatusException(String message) {
        super(message);
    }
}
