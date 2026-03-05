package com.bank.exo.domain.model;

import com.bank.exo.constant.AccountType;
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
    public String accountTypeLabel() {
        return AccountType.SAVINGS.getLabel();
    }
}
