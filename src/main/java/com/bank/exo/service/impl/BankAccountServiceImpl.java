package com.bank.exo.service.impl;

import com.bank.exo.api.dto.BankStatementDto;
import com.bank.exo.api.dto.OperationDto;
import com.bank.exo.constant.AccountType;
import com.bank.exo.constant.OperationType;
import com.bank.exo.exception.BankAccountNotFoundException;
import com.bank.exo.exception.InsufficientFundsException;
import com.bank.exo.exception.OverdraftLimitException;
import com.bank.exo.exception.OverdraftNotAllowedException;
import com.bank.exo.model.BankAccount;
import com.bank.exo.model.Operation;
import com.bank.exo.model.SavingsAccount;
import com.bank.exo.repository.BankAccountRepository;
import com.bank.exo.service.BankAccountService;
import com.bank.exo.service.OperationService;
import com.bank.exo.util.AmountValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final OperationService operationService;

    @Override
    public BankAccount deposit(Long id, BigDecimal amount) {
        AmountValidator.validate(amount);
        BankAccount account = bankAccountRepository.findById(id)
                .orElseThrow(BankAccountNotFoundException::new);

        account.setBalance(account.getBalance().add(amount));
        operationService.saveOperation(account, amount, OperationType.DEPOSIT);
        return bankAccountRepository.save(account);
    }

    @Override
    public BankAccount withdraw(Long id, BigDecimal amount) {
        AmountValidator.validate(amount);

        BankAccount account = bankAccountRepository.findById(id)
                .orElseThrow(BankAccountNotFoundException::new);

        if (amount.compareTo(account.getBalance()) > 0) {
            if (account.getOverdraftLimit() != null) {
                BigDecimal finalBalance = account.getBalance().subtract(amount);
                if (finalBalance.compareTo(account.getOverdraftLimit().negate()) < 0) {
                    throw new OverdraftLimitException();
                }
            } else {
                throw new InsufficientFundsException();
            }
        }
        account.setBalance(account.getBalance().subtract(amount));
        operationService.saveOperation(account, amount, OperationType.WITHDRAWAL);
        return bankAccountRepository.save(account);
    }

    @Override
    public BankAccount create() {
        BankAccount account = new BankAccount()
                .setAccountNumber(UUID.randomUUID().toString())
                .setBalance(BigDecimal.ZERO);
        return bankAccountRepository.save(account);
    }

    @Override
    public BankAccount updateOverdraft(Long id, BigDecimal amount) {
        BankAccount account = bankAccountRepository.findById(id)
                .orElseThrow(BankAccountNotFoundException::new);


        if (account instanceof SavingsAccount) {
            throw new OverdraftNotAllowedException();
        }

        if (amount != null) {
            AmountValidator.checkFormat(amount);

        }

        account.setOverdraftLimit(amount);
        return bankAccountRepository.save(account);
    }

    @Transactional
    public BankStatementDto getStatement(Long id) {
        BankAccount account = bankAccountRepository.findById(id)
                .orElseThrow(BankAccountNotFoundException::new);


        LocalDateTime dateLimit = LocalDateTime.now().minusMonths(1);

        List<OperationDto> operationDtos = operationService.findByAccountId(id)
                .stream()
                .filter(op -> op.getDate().isAfter(dateLimit))
                .sorted(Comparator.comparing(Operation::getDate).reversed())
                .map(op -> {
                    OperationDto dto = new OperationDto();
                    dto.setType(op.getType().getLabel());
                    dto.setAmount(op.getAmount());
                    dto.setDate(op.getDate());
                    return dto;
                })
                .toList();


        BankStatementDto statementDto = new BankStatementDto();
        statementDto.setOperationDtos(operationDtos);
        statementDto.setBalance(account.getBalance());
        statementDto.setEmissionDate(LocalDateTime.now());
        statementDto.setAccountType(account instanceof SavingsAccount ?
                AccountType.SAVINGS.getLabel() :
                AccountType.CURRENT.getLabel());
        statementDto.setBalance(account.getBalance());
        statementDto.setEmissionDate(LocalDateTime.now());

        return statementDto;

    }
}
