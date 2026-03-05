package com.bank.exo.exception;

import com.bank.exo.api.shared.ErrorConstants;

public class OverdraftLimitException extends RuntimeException {
    public OverdraftLimitException() {
        super(ErrorConstants.OVERDRAFT_EXCEEDED);
    }
}
