package com.bank.exo.exception;

import com.bank.exo.api.shared.ErrorConstants;

public class CantBeZeroException extends RuntimeException {
    public CantBeZeroException() {
        super(ErrorConstants.CANT_BE_ZERO);
    }
}
