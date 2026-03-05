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
import com.bank.exo.exception.OverdraftNotAllowedException;
import com.bank.exo.util.AmountValidator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CurrentAccountUseCaseImpl extends AbstractAccountUseCaseSupport implements CurrentAccountUseCase {

    private final StatementMapper statementMapper;

    public CurrentAccountUseCaseImpl(AccountRepositoryPort accountRepository,
                                     OperationRepositoryPort operationRepository,
                                     StatementMapper statementMapper) {
        super(accountRepository, operationRepository);
        this.statementMapper = statementMapper;
    }

    @Override
    public AbstractBankAccount create() {
        CurrentAccount account = new CurrentAccount(null, UUID.randomUUID().toString(), BigDecimal.ZERO, null);
        return save(account);
    }

    @Override
    public AbstractBankAccount deposit(Long id, BigDecimal amount) {
        validateAmount(amount);
        AbstractBankAccount account = findAccount(id);

        // Changement: la règle de dépôt est portée par l'entité (polymorphisme).
        account.assertCanDeposit(amount);
        account.addToBalance(amount);
        saveOperation(account.getId(), amount, OperationType.DEPOSIT);
        return save(account);
    }

    @Override
    public AbstractBankAccount withdraw(Long id, BigDecimal amount) {
        validateAmount(amount);
        AbstractBankAccount account = findAccount(id);

        // Changement: suppression des conditions métier spécifiques dans l'application.
        account.assertCanWithdraw(amount);
        account.subtractFromBalance(amount);
        saveOperation(account.getId(), amount, OperationType.WITHDRAWAL);
        return save(account);
    }

    @Override
    public AbstractBankAccount updateOverdraft(Long id, BigDecimal amount) {
        AbstractBankAccount account = findAccount(id);
        if (!(account instanceof CurrentAccount)) {
            throw new OverdraftNotAllowedException();
        }

        if (amount != null) {
            AmountValidator.checkFormat(amount);
        }

        account.updateOverdraftLimit(amount);
        return save(account);
    }

    @Override
    public BankStatementDto getStatement(Long id) {
        AbstractBankAccount account = findAccount(id);
        LocalDateTime dateLimit = LocalDateTime.now().minusMonths(1);

        List<Operation> operations = operationRepository().findByAccountId(id)
                .stream()
                .filter(op -> op.getDate().isAfter(dateLimit))
                .toList();

        return statementMapper.toDto(account, operations);
    }
}
