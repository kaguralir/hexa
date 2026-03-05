package com.bank.exo.exception;

import com.bank.exo.api.shared.ErrorConstants;

public class CantBeNullException extends RuntimeException {
    public CantBeNullException() {
        super(ErrorConstants.CANT_BE_NULL);
    }
}
