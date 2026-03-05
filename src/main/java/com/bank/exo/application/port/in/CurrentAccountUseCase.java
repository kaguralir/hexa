package com.bank.exo.application.port.in;

import com.bank.exo.api.dto.BankStatementDto;
import com.bank.exo.domain.model.AbstractBankAccount;

import java.math.BigDecimal;

public interface CurrentAccountUseCase {
    AbstractBankAccount create();

    AbstractBankAccount deposit(Long id, BigDecimal amount);

    AbstractBankAccount withdraw(Long id, BigDecimal amount);

    AbstractBankAccount updateOverdraft(Long id, BigDecimal amount);

    BankStatementDto getStatement(Long id);
}
