package com.bank.exo.domain.model;

import com.bank.exo.constant.AccountType;
import com.bank.exo.exception.InsufficientFundsException;
import com.bank.exo.exception.SavingsAccountLimitException;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class SavingsAccount extends AbstractBankAccount {
    private final BigDecimal depositLimit;

    public SavingsAccount(Long id, String accountNumber, BigDecimal balance, BigDecimal depositLimit) {
        super(id, accountNumber, balance, null);
        this.depositLimit = depositLimit;
    }


    @Override
    public void assertCanDeposit(BigDecimal amount) {
        if (getBalance().add(amount).compareTo(depositLimit) > 0) {
            throw new SavingsAccountLimitException();
        }
    }

    @Override
    public void assertCanWithdraw(BigDecimal amount) {
        if (amount.compareTo(getBalance()) > 0) {
            throw new InsufficientFundsException();
        }
    }

    @Override
    public BigDecimal depositLimitOrNull() {
        return depositLimit;
    }

    @Override
    public String accountTypeLabel() {
        return AccountType.SAVINGS.getLabel();
    }
}
