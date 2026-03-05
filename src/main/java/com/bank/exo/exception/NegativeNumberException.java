package com.bank.exo.exception;

import com.bank.exo.api.shared.ErrorConstants;

public class NegativeNumberException extends RuntimeException {
    public NegativeNumberException() {
        super(ErrorConstants.NEGATIVE_NUMBER);
    }
}
