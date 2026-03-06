package com.bank.exo.application.usecase;

import com.bank.exo.application.mapper.StatementMapper;
import com.bank.exo.application.port.in.CurrentAccountUseCase;
import com.bank.exo.application.port.out.AccountRepositoryPort;
import com.bank.exo.application.result.StatementResult;
import com.bank.exo.application.service.AccountLoader;
import com.bank.exo.application.service.OperationRecorder;
import com.bank.exo.domain.OperationType;
import com.bank.exo.domain.model.AbstractBankAccount;
import com.bank.exo.domain.model.CurrentAccount;
import com.bank.exo.domain.model.Operation;
import com.bank.exo.domain.valueobject.Amount;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CurrentAccountUseCaseImpl implements CurrentAccountUseCase {

    private final AccountRepositoryPort accountRepository;
    private final AccountLoader accountLoader;
    private final OperationRecorder operationRecorder;
    private final StatementMapper statementMapper;

    public CurrentAccountUseCaseImpl(AccountRepositoryPort accountRepository,
                                     AccountLoader accountLoader,
                                     OperationRecorder operationRecorder,
                                     StatementMapper statementMapper) {
        this.accountRepository = accountRepository;
        this.accountLoader = accountLoader;
        this.operationRecorder = operationRecorder;
        this.statementMapper = statementMapper;
    }

    @Override
    public AbstractBankAccount create() {
        CurrentAccount account = new CurrentAccount(null, UUID.randomUUID().toString(), BigDecimal.ZERO, null);
        return accountRepository.save(account);
    }

    @Override
    public AbstractBankAccount deposit(Long id, Amount amount) {
        AbstractBankAccount account = accountLoader.findById(id);
        account.assertCanDeposit(amount);
        account.addToBalance(amount);
        operationRecorder.record(account.getId(), amount, OperationType.DEPOSIT);
        return accountRepository.save(account);
    }

    @Override
    public AbstractBankAccount withdraw(Long id, Amount amount) {
        AbstractBankAccount account = accountLoader.findById(id);
        account.assertCanWithdraw(amount);
        account.subtractFromBalance(amount);
        operationRecorder.record(account.getId(), amount, OperationType.WITHDRAWAL);
        return accountRepository.save(account);
    }

    @Override
    public AbstractBankAccount updateOverdraft(Long id, BigDecimal overdraftLimit) {
        AbstractBankAccount account = accountLoader.findById(id);
        // Domain entity carries the rule: throws OverdraftNotAllowedException if not a CurrentAccount.
        account.assertCanUpdateOverdraft();
        if (overdraftLimit != null) {
            Amount.of(overdraftLimit); // validates positive if provided
        }
        account.updateOverdraftLimit(overdraftLimit);
        return accountRepository.save(account);
    }

    @Override
    public StatementResult getStatement(Long id) {
        AbstractBankAccount account = accountLoader.findById(id);
        LocalDateTime dateLimit = LocalDateTime.now().minusMonths(1);

        List<Operation> operations = operationRecorder.repository().findByAccountId(id)
                .stream()
                .filter(op -> op.getDate().isAfter(dateLimit))
                .toList();

        return statementMapper.toResult(account, operations);
    }
}
