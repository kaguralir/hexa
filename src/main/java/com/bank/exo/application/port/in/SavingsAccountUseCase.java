package com.bank.exo.application.port.in;

import com.bank.exo.domain.model.AbstractBankAccount;
import com.bank.exo.domain.valueobject.Amount;

public interface SavingsAccountUseCase {
    AbstractBankAccount create();

    AbstractBankAccount deposit(Long id, Amount amount);

    AbstractBankAccount withdraw(Long id, Amount amount);
}
