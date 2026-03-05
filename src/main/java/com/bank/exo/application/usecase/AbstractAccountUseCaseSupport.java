package com.bank.exo.application.usecase;

import com.bank.exo.application.port.out.AccountRepositoryPort;
import com.bank.exo.application.port.out.OperationRepositoryPort;
import com.bank.exo.constant.OperationType;
import com.bank.exo.domain.model.AbstractBankAccount;
import com.bank.exo.domain.model.Operation;
import com.bank.exo.exception.BankAccountNotFoundException;
import com.bank.exo.util.AmountValidator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Changement d'architecture: classe abstraite de support applicatif.
 *
 * SRP: centralise les comportements transverses (chargement de compte,
 * validation de montant, traçabilité des opérations).
 *
 * DRY: évite la duplication entre use cases courant/épargne.
 */
public abstract class AbstractAccountUseCaseSupport {

    private final AccountRepositoryPort accountRepository;
    private final OperationRepositoryPort operationRepository;

    protected AbstractAccountUseCaseSupport(AccountRepositoryPort accountRepository,
                                            OperationRepositoryPort operationRepository) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
    }

    protected AbstractBankAccount findAccount(Long id) {
        return accountRepository.findById(id).orElseThrow(BankAccountNotFoundException::new);
    }

    protected AbstractBankAccount save(AbstractBankAccount account) {
        return accountRepository.save(account);
    }

    protected void validateAmount(BigDecimal amount) {
        AmountValidator.validate(amount);
    }

    protected void saveOperation(Long accountId, BigDecimal amount, OperationType type) {
        operationRepository.save(Operation.builder()
                .accountId(accountId)
                .amount(amount)
                .type(type)
                .date(LocalDateTime.now())
                .build());
    }

    protected OperationRepositoryPort operationRepository() {
        return operationRepository;
    }
}
