package com.bank.exo.domain.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException() {
        super("Bank account not found");
    }
}
