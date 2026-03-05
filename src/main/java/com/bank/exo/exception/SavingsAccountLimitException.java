package com.bank.exo.exception;

import com.bank.exo.api.shared.ErrorConstants;

public class SavingsAccountLimitException extends RuntimeException {
    public SavingsAccountLimitException() {
        super(ErrorConstants.SAVINGS_ACCOUNT_LIMIT_EXCEEDED);
    }
}
