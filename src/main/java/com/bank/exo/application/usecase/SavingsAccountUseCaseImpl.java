package com.bank.exo.application.usecase;

import com.bank.exo.application.port.in.SavingsAccountUseCase;
import com.bank.exo.application.port.out.AccountRepositoryPort;
import com.bank.exo.application.service.AccountLoader;
import com.bank.exo.application.service.OperationRecorder;
import com.bank.exo.domain.OperationType;
import com.bank.exo.domain.model.AbstractBankAccount;
import com.bank.exo.domain.model.SavingsAccount;
import com.bank.exo.domain.valueobject.Amount;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class SavingsAccountUseCaseImpl implements SavingsAccountUseCase {

    private static final BigDecimal DEFAULT_CEILING = BigDecimal.valueOf(22950);

    private final AccountRepositoryPort accountRepository;
    private final AccountLoader accountLoader;
    private final OperationRecorder operationRecorder;

    public SavingsAccountUseCaseImpl(AccountRepositoryPort accountRepository,
                                     AccountLoader accountLoader,
                                     OperationRecorder operationRecorder) {
        this.accountRepository = accountRepository;
        this.accountLoader = accountLoader;
        this.operationRecorder = operationRecorder;
    }

    @Override
    public AbstractBankAccount create() {
        SavingsAccount account = new SavingsAccount(null, UUID.randomUUID().toString(), BigDecimal.ZERO, DEFAULT_CEILING);
        return accountRepository.save(account);
    }

    @Override
    public AbstractBankAccount deposit(Long id, Amount amount) {
        SavingsAccount account = accountLoader.findSavingsAccountById(id);
        account.assertCanDeposit(amount);
        account.addToBalance(amount);
        operationRecorder.record(account.getId(), amount, OperationType.DEPOSIT);
        return accountRepository.save(account);
    }

    @Override
    public AbstractBankAccount withdraw(Long id, Amount amount) {
        SavingsAccount account = accountLoader.findSavingsAccountById(id);
        account.assertCanWithdraw(amount);
        account.subtractFromBalance(amount);
        operationRecorder.record(account.getId(), amount, OperationType.WITHDRAWAL);
        return accountRepository.save(account);
    }
}
