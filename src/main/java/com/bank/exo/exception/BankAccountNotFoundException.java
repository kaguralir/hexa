package com.bank.exo.exception;

import com.bank.exo.api.shared.ErrorConstants;

public class BankAccountNotFoundException extends RuntimeException {
    public BankAccountNotFoundException() {
        super(ErrorConstants.ACCOUNT_NOT_FOUND);
    }
}
