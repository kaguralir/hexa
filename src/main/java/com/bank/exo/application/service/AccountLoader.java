package com.bank.exo.application.service;

import com.bank.exo.application.port.out.AccountRepositoryPort;
import com.bank.exo.domain.exception.AccountNotFoundException;
import com.bank.exo.domain.model.AbstractBankAccount;
import com.bank.exo.domain.model.SavingsAccount;
import org.springframework.stereotype.Component;

/**
 * Application service responsible for loading accounts from the repository.
 * Composed into use cases instead of inherited from a base class.
 */
@Component
public class AccountLoader {

    private final AccountRepositoryPort accountRepository;

    public AccountLoader(AccountRepositoryPort accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AbstractBankAccount findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(AccountNotFoundException::new);
    }

    public SavingsAccount findSavingsAccountById(Long id) {
        return accountRepository.findSavingsAccountById(id)
                .orElseThrow(AccountNotFoundException::new);
    }
}
