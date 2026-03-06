package com.bank.exo.domain.valueobject;

import com.bank.exo.domain.exception.InvalidAmountException;

import java.math.BigDecimal;

/**
 * Value object representing a valid transaction amount.
 * Invariants enforced at construction: not null, not zero, strictly positive.
 * Use for deposits, withdrawals, and operation recording — not for balances,
 * which can legitimately be zero or negative (overdraft).
 */
public record Amount(BigDecimal value) {

    public Amount {
        if (value == null) {
            throw new InvalidAmountException("Amount is required");
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAmountException("Amount cannot be negative");
        }
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            throw new InvalidAmountException("Amount cannot be zero");
        }
    }

    public static Amount of(BigDecimal value) {
        return new Amount(value);
    }
}
