package com.bank.exo.application.port.in;

import com.bank.exo.application.result.StatementResult;
import com.bank.exo.domain.model.AbstractBankAccount;
import com.bank.exo.domain.valueobject.Amount;

import java.math.BigDecimal;

public interface CurrentAccountUseCase {
    AbstractBankAccount create();

    AbstractBankAccount deposit(Long id, Amount amount);

    AbstractBankAccount withdraw(Long id, Amount amount);

    /**
     * @param overdraftLimit null removes the overdraft authorization
     */
    AbstractBankAccount updateOverdraft(Long id, BigDecimal overdraftLimit);

    StatementResult getStatement(Long id);
}
