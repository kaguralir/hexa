package com.bank.exo.service;

import com.bank.exo.model.SavingsAccount;

import java.math.BigDecimal;

public interface SavingsAccountService {
    SavingsAccount deposit(Long id, BigDecimal amount);

    SavingsAccount withdraw(Long id, BigDecimal amount);

    SavingsAccount create();

}
