package com.bank.exo.application.port.in;

import com.bank.exo.domain.model.AbstractBankAccount;

import java.math.BigDecimal;

public interface SavingsAccountUseCase {
    AbstractBankAccount create();

    AbstractBankAccount deposit(Long id, BigDecimal amount);

    AbstractBankAccount withdraw(Long id, BigDecimal amount);
}
