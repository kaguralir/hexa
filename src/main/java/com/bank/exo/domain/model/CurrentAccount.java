package com.bank.exo.domain.model;

import com.bank.exo.domain.AccountType;
import com.bank.exo.domain.exception.InsufficientFundsException;
import com.bank.exo.domain.exception.OverdraftLimitExceededException;
import com.bank.exo.domain.valueobject.Amount;

import java.math.BigDecimal;

public class CurrentAccount extends AbstractBankAccount {

    public CurrentAccount(Long id, String accountNumber, BigDecimal balance, BigDecimal overdraftLimit) {
        super(id, accountNumber, balance, overdraftLimit);
    }

    @Override
    public void assertCanDeposit(Amount amount) {
        // No deposit ceiling on current accounts.
    }

    @Override
    public void assertCanWithdraw(Amount amount) {
        if (amount.value().compareTo(getBalance()) <= 0) {
            return;
        }

        if (getOverdraftLimit() == null) {
            throw new InsufficientFundsException();
        }

        BigDecimal finalBalance = getBalance().subtract(amount.value());
        if (finalBalance.compareTo(getOverdraftLimit().negate()) < 0) {
            throw new OverdraftLimitExceededException();
        }
    }

    @Override
    public void assertCanUpdateOverdraft() {
        // Allowed for current accounts — no exception thrown.
    }

    @Override
    public String accountTypeLabel() {
        return AccountType.CURRENT.getLabel();
    }
}
