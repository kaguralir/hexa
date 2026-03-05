package com.bank.exo.application.usecase;

import com.bank.exo.application.port.in.SavingsAccountUseCase;
import com.bank.exo.application.port.out.AccountRepositoryPort;
import com.bank.exo.application.port.out.OperationRepositoryPort;
import com.bank.exo.constant.OperationType;
import com.bank.exo.domain.model.AbstractBankAccount;
import com.bank.exo.domain.model.Operation;
import com.bank.exo.domain.model.SavingsAccount;
import com.bank.exo.exception.BankAccountNotFoundException;
import com.bank.exo.exception.InsufficientFundsException;
import com.bank.exo.exception.SavingsAccountLimitException;
import com.bank.exo.util.AmountValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SavingsAccountUseCaseImpl implements SavingsAccountUseCase {

    private static final BigDecimal DEFAULT_LIMIT = BigDecimal.valueOf(22950);

    private final AccountRepositoryPort accountRepository;
    private final OperationRepositoryPort operationRepository;

    @Override
    public AbstractBankAccount create() {
        SavingsAccount account = new SavingsAccount(null, UUID.randomUUID().toString(), BigDecimal.ZERO, DEFAULT_LIMIT);
        return accountRepository.save(account);
    }

    @Override
    public AbstractBankAccount deposit(Long id, BigDecimal amount) {
        AmountValidator.validate(amount);
        SavingsAccount account = findSavingsAccount(id);

        if (account.getBalance().add(amount).compareTo(account.getDepositLimit()) > 0) {
            throw new SavingsAccountLimitException();
        }

        account.addToBalance(amount);
        saveOperation(account.getId(), amount, OperationType.DEPOSIT);
        return accountRepository.save(account);
    }

    @Override
    public AbstractBankAccount withdraw(Long id, BigDecimal amount) {
        AmountValidator.validate(amount);
        SavingsAccount account = findSavingsAccount(id);

        if (amount.compareTo(account.getBalance()) > 0) {
            throw new InsufficientFundsException();
        }

        account.subtractFromBalance(amount);
        saveOperation(account.getId(), amount, OperationType.WITHDRAWAL);
        return accountRepository.save(account);
    }

    private SavingsAccount findSavingsAccount(Long id) {
        AbstractBankAccount account = accountRepository.findById(id)
                .orElseThrow(BankAccountNotFoundException::new);

        if (!(account instanceof SavingsAccount savingsAccount)) {
            throw new BankAccountNotFoundException();
        }

        return savingsAccount;
    }

    private void saveOperation(Long accountId, BigDecimal amount, OperationType type) {
        operationRepository.save(Operation.builder()
                .accountId(accountId)
                .amount(amount)
                .type(type)
                .date(LocalDateTime.now())
                .build());
    }
}
