package com.insurance.exception;

public class InvalidBusinessStateException extends RuntimeException {
    public InvalidBusinessStateException(String message) {
        super(message);
    }
}
