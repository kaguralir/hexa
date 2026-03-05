package com.bank.exo.application.port.out;

import com.bank.exo.domain.model.AbstractBankAccount;

import java.util.Optional;

public interface AccountRepositoryPort {
    AbstractBankAccount save(AbstractBankAccount account);

    Optional<AbstractBankAccount> findById(Long id);

    void deleteAll();
}
