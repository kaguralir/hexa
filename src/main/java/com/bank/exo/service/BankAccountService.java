package com.bank.exo.service;

import com.bank.exo.api.dto.BankStatementDto;
import com.bank.exo.model.BankAccount;

import java.math.BigDecimal;

public interface BankAccountService {
    BankAccount deposit(Long id, BigDecimal amount);

    BankAccount withdraw(Long id, BigDecimal amount);

    BankAccount create();

    BankAccount updateOverdraft(Long id, BigDecimal amount);

    BankStatementDto getStatement(Long id);


}
