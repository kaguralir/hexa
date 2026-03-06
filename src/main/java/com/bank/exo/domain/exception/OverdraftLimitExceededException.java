package com.bank.exo.domain.exception;

public class OverdraftLimitExceededException extends RuntimeException {
    public OverdraftLimitExceededException() {
        super("Amount exceeds overdraft limit");
    }
}
