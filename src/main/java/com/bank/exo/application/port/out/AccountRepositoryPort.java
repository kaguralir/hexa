package com.bank.exo.application.port.out;

import com.bank.exo.domain.model.AbstractBankAccount;
import com.bank.exo.domain.model.SavingsAccount;

import java.util.Optional;

public interface AccountRepositoryPort {
    AbstractBankAccount save(AbstractBankAccount account);

    Optional<AbstractBankAccount> findById(Long id);

    /**
     * Returns the account only if it is a SavingsAccount.
     * The adapter resolves the type; use cases stay free of instanceof.
     */
    Optional<SavingsAccount> findSavingsAccountById(Long id);
}
