package com.bank.exo.util;

import com.bank.exo.exception.CantBeNullException;
import com.bank.exo.exception.CantBeZeroException;
import com.bank.exo.exception.NegativeNumberException;

import java.math.BigDecimal;

public final class AmountValidator {

    private AmountValidator() {
    }

    public static void validate(BigDecimal amount) {
        if (amount == null) {
            throw new CantBeNullException();
        }
        checkFormat(amount);
    }

    public static void checkFormat(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeNumberException();
        }
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            throw new CantBeZeroException();
        }
    }
}
