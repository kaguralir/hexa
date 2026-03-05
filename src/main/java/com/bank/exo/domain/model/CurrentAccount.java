package com.bank.exo.domain.model;

import com.bank.exo.constant.AccountType;

import java.math.BigDecimal;

public class CurrentAccount extends AbstractBankAccount {

    public CurrentAccount(Long id, String accountNumber, BigDecimal balance, BigDecimal overdraftLimit) {
        super(id, accountNumber, balance, overdraftLimit);
    }

    @Override
    public String accountTypeLabel() {
        return AccountType.CURRENT.getLabel();
    }
}
