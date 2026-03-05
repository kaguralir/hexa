package com.bank.exo.exception;

import com.bank.exo.api.shared.ErrorConstants;

public class OverdraftNotAllowedException extends RuntimeException {
    public OverdraftNotAllowedException() {
        super(ErrorConstants.OVERDRAFT_NOT_ALLOWED);
    }
}
