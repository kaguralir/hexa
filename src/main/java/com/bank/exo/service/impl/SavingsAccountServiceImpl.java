package com.bank.exo.service.impl;

import com.bank.exo.constant.OperationType;
import com.bank.exo.exception.BankAccountNotFoundException;
import com.bank.exo.exception.InsufficientFundsException;
import com.bank.exo.exception.SavingsAccountLimitException;
import com.bank.exo.model.SavingsAccount;
import com.bank.exo.repository.SavingsAccountRepository;
import com.bank.exo.service.OperationService;
import com.bank.exo.service.SavingsAccountService;
import com.bank.exo.util.AmountValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SavingsAccountServiceImpl implements SavingsAccountService {

    private final SavingsAccountRepository savingsAccountRepository;
    private final OperationService operationService;


    @Override
    public SavingsAccount create() {
        SavingsAccount account = new SavingsAccount();

        account.setAccountNumber(UUID.randomUUID().toString());
        account.setBalance(BigDecimal.ZERO);
        account.setDepositLimit(BigDecimal.valueOf(22950));

        return savingsAccountRepository.save(account);
    }

    @Override
    public SavingsAccount deposit(Long id, BigDecimal amount) {
        AmountValidator.validate(amount);
        SavingsAccount account = savingsAccountRepository.findById(id)
                .orElseThrow(BankAccountNotFoundException::new);

        if (account.getBalance().add(amount).compareTo(account.getDepositLimit()) > 0) {
            throw new SavingsAccountLimitException();
        }

        account.setBalance(account.getBalance().add(amount));
        operationService.saveOperation(account, amount, OperationType.DEPOSIT);
        return savingsAccountRepository.save(account);
    }

    @Override
    public SavingsAccount withdraw(Long id, BigDecimal amount) {
        AmountValidator.validate(amount);

        SavingsAccount account = savingsAccountRepository.findById(id)
                .orElseThrow(BankAccountNotFoundException::new);

        if (amount.compareTo(account.getBalance()) > 0) {
            throw new InsufficientFundsException();
        }

        account.setBalance(account.getBalance().subtract(amount));
        operationService.saveOperation(account, amount, OperationType.WITHDRAWAL);
        return savingsAccountRepository.save(account);
    }

}
