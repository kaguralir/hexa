package com.bank.exo.domain.model;

import com.bank.exo.constant.AccountType;
import com.bank.exo.exception.InsufficientFundsException;
import com.bank.exo.exception.OverdraftLimitException;

import java.math.BigDecimal;

public class CurrentAccount extends AbstractBankAccount {

    public CurrentAccount(Long id, String accountNumber, BigDecimal balance, BigDecimal overdraftLimit) {
        super(id, accountNumber, balance, overdraftLimit);
    }

    @Override
    public void assertCanDeposit(BigDecimal amount) {
        // Pas de contrainte spécifique côté compte courant.
    }

    @Override
    public void assertCanWithdraw(BigDecimal amount) {
        if (amount.compareTo(getBalance()) <= 0) {
            return;
        }

        if (getOverdraftLimit() == null) {
            throw new InsufficientFundsException();
        }

        BigDecimal finalBalance = getBalance().subtract(amount);
        if (finalBalance.compareTo(getOverdraftLimit().negate()) < 0) {
            throw new OverdraftLimitException();
        }
    }

    @Override
    public String accountTypeLabel() {
        return AccountType.CURRENT.getLabel();
    }
}
