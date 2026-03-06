package com.bank.exo.domain.exception;

/**
 * Thrown when a transaction amount violates domain rules:
 * null, zero, or negative values are not valid amounts.
 */
public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException(String message) {
        super(message);
    }
}
