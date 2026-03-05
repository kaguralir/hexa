package com.bank.exo.application.usecase;

import com.bank.exo.application.port.in.SavingsAccountUseCase;
import com.bank.exo.application.port.out.AccountRepositoryPort;
import com.bank.exo.application.port.out.OperationRepositoryPort;
import com.bank.exo.constant.OperationType;
import com.bank.exo.domain.model.AbstractBankAccount;
import com.bank.exo.domain.model.SavingsAccount;
import com.bank.exo.exception.BankAccountNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class SavingsAccountUseCaseImpl extends AbstractAccountUseCaseSupport implements SavingsAccountUseCase {

    private static final BigDecimal DEFAULT_LIMIT = BigDecimal.valueOf(22950);

    public SavingsAccountUseCaseImpl(AccountRepositoryPort accountRepository,
                                     OperationRepositoryPort operationRepository) {
        super(accountRepository, operationRepository);
    }

    @Override
    public AbstractBankAccount create() {
        SavingsAccount account = new SavingsAccount(null, UUID.randomUUID().toString(), BigDecimal.ZERO, DEFAULT_LIMIT);
        return save(account);
    }

    @Override
    public AbstractBankAccount deposit(Long id, BigDecimal amount) {
        validateAmount(amount);
        SavingsAccount account = findSavingsAccount(id);

        account.assertCanDeposit(amount);
        account.addToBalance(amount);
        saveOperation(account.getId(), amount, OperationType.DEPOSIT);
        return save(account);
    }

    @Override
    public AbstractBankAccount withdraw(Long id, BigDecimal amount) {
        validateAmount(amount);
        SavingsAccount account = findSavingsAccount(id);

        account.assertCanWithdraw(amount);
        account.subtractFromBalance(amount);
        saveOperation(account.getId(), amount, OperationType.WITHDRAWAL);
        return save(account);
    }

    private SavingsAccount findSavingsAccount(Long id) {
        AbstractBankAccount account = findAccount(id);
        if (!(account instanceof SavingsAccount savingsAccount)) {
            throw new BankAccountNotFoundException();
        }
        return savingsAccount;
    }
}
