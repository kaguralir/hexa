package com.bank.exo.application.usecase;

import com.bank.exo.api.dto.BankStatementDto;
import com.bank.exo.application.mapper.StatementMapper;
import com.bank.exo.application.port.in.CurrentAccountUseCase;
import com.bank.exo.application.port.out.AccountRepositoryPort;
import com.bank.exo.application.port.out.OperationRepositoryPort;
import com.bank.exo.constant.OperationType;
import com.bank.exo.domain.model.AbstractBankAccount;
import com.bank.exo.domain.model.CurrentAccount;
import com.bank.exo.domain.model.Operation;
import com.bank.exo.domain.model.SavingsAccount;
import com.bank.exo.exception.BankAccountNotFoundException;
import com.bank.exo.exception.InsufficientFundsException;
import com.bank.exo.exception.OverdraftLimitException;
import com.bank.exo.exception.OverdraftNotAllowedException;
import com.bank.exo.util.AmountValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CurrentAccountUseCaseImpl implements CurrentAccountUseCase {

    private final AccountRepositoryPort accountRepository;
    private final OperationRepositoryPort operationRepository;
    private final StatementMapper statementMapper;

    @Override
    public AbstractBankAccount create() {
        CurrentAccount account = new CurrentAccount(null, UUID.randomUUID().toString(), BigDecimal.ZERO, null);
        return accountRepository.save(account);
    }

    @Override
    public AbstractBankAccount deposit(Long id, BigDecimal amount) {
        AmountValidator.validate(amount);
        AbstractBankAccount account = findAccount(id);
        account.addToBalance(amount);
        saveOperation(account.getId(), amount, OperationType.DEPOSIT);
        return accountRepository.save(account);
    }

    @Override
    public AbstractBankAccount withdraw(Long id, BigDecimal amount) {
        AmountValidator.validate(amount);
        AbstractBankAccount account = findAccount(id);

        if (amount.compareTo(account.getBalance()) > 0) {
            if (account.getOverdraftLimit() == null) {
                throw new InsufficientFundsException();
            }
            BigDecimal finalBalance = account.getBalance().subtract(amount);
            if (finalBalance.compareTo(account.getOverdraftLimit().negate()) < 0) {
                throw new OverdraftLimitException();
            }
        }

        account.subtractFromBalance(amount);
        saveOperation(account.getId(), amount, OperationType.WITHDRAWAL);
        return accountRepository.save(account);
    }

    @Override
    public AbstractBankAccount updateOverdraft(Long id, BigDecimal amount) {
        AbstractBankAccount account = findAccount(id);
        if (account instanceof SavingsAccount) {
            throw new OverdraftNotAllowedException();
        }

        if (amount != null) {
            AmountValidator.checkFormat(amount);
        }

        account.updateOverdraftLimit(amount);
        return accountRepository.save(account);
    }

    @Override
    public BankStatementDto getStatement(Long id) {
        AbstractBankAccount account = findAccount(id);
        LocalDateTime dateLimit = LocalDateTime.now().minusMonths(1);

        // Changement: le use case dépend d'un port, pas d'un repository Spring/JPA (inversion de dépendance).
        List<Operation> operations = operationRepository.findByAccountId(id)
                .stream()
                .filter(op -> op.getDate().isAfter(dateLimit))
                .toList();

        return statementMapper.toDto(account, operations);
    }

    private AbstractBankAccount findAccount(Long id) {
        return accountRepository.findById(id).orElseThrow(BankAccountNotFoundException::new);
    }

    private void saveOperation(Long accountId, BigDecimal amount, OperationType type) {
        Operation operation = Operation.builder()
                .accountId(accountId)
                .amount(amount)
                .type(type)
                .date(LocalDateTime.now())
                .build();
        operationRepository.save(operation);
    }
}
