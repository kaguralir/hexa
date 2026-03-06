package com.bank.exo.domain.exception;

public class OverdraftNotAllowedException extends RuntimeException {
    public OverdraftNotAllowedException() {
        super("Overdraft is not allowed for this account type");
    }
}
