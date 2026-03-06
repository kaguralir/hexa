package com.bank.exo.domain.model;

import com.bank.exo.domain.AccountType;
import com.bank.exo.domain.exception.DepositLimitExceededException;
import com.bank.exo.domain.exception.InsufficientFundsException;
import com.bank.exo.domain.valueobject.Amount;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Optional;

@Getter
public class SavingsAccount extends AbstractBankAccount {
    private final BigDecimal depositCeiling;

    public SavingsAccount(Long id, String accountNumber, BigDecimal balance, BigDecimal depositCeiling) {
        super(id, accountNumber, balance, null);
        this.depositCeiling = depositCeiling;
    }

    @Override
    public void assertCanDeposit(Amount amount) {
        if (getBalance().add(amount.value()).compareTo(depositCeiling) > 0) {
            throw new DepositLimitExceededException();
        }
    }

    @Override
    public void assertCanWithdraw(Amount amount) {
        if (amount.value().compareTo(getBalance()) > 0) {
            throw new InsufficientFundsException();
        }
    }

    @Override
    public Optional<BigDecimal> depositLimit() {
        return Optional.of(depositCeiling);
    }

    @Override
    public String accountTypeLabel() {
        return AccountType.SAVINGS.getLabel();
    }
}
