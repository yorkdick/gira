package com.rayfay.gira.exception;

public class WipLimitExceededException extends RuntimeException {
    public WipLimitExceededException(String message) {
        super(message);
    }
}