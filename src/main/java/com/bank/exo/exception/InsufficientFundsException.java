package com.bank.exo.exception;

import com.bank.exo.api.shared.ErrorConstants;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException() {
        super(ErrorConstants.INSUFFICIENT_FUNDS);
    }
}
