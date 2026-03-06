package com.bank.exo.domain.exception;

public class DepositLimitExceededException extends RuntimeException {
    public DepositLimitExceededException() {
        super("Deposit amount exceeds the account ceiling");
    }
}
