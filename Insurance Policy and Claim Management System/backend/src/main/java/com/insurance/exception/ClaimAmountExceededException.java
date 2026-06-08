package com.insurance.exception;

public class ClaimAmountExceededException extends RuntimeException {
    public ClaimAmountExceededException(String message) {
        super(message);
    }
}
