package com.bank.exo;

import com.bank.exo.api.dto.BankStatementDto;
import com.bank.exo.constant.AccountType;
import com.bank.exo.constant.OperationType;
import com.bank.exo.exception.*;
import com.bank.exo.model.BankAccount;
import com.bank.exo.model.Operation;
import com.bank.exo.model.SavingsAccount;
import com.bank.exo.repository.BankAccountRepository;
import com.bank.exo.service.impl.BankAccountServiceImpl;
import com.bank.exo.service.impl.OperationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceImplTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @InjectMocks
    private BankAccountServiceImpl
            bankAccountService;

    @Mock
    private OperationServiceImpl operationService;

    @Test
    void should_deposit_money_successfully_when_amount_positive() {
        BankAccount account = new BankAccount()
                .setId(1L)
                .setBalance(BigDecimal.valueOf(100));

        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(bankAccountRepository.save(any())).thenReturn(account);

        BankAccount result = bankAccountService.deposit(1L, BigDecimal.valueOf(50));

        assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(150));
    }

    @Test
    void should_withdraw_money_successfully_when_fund_sufficient() {
        BankAccount account = new BankAccount()
                .setId(1L)
                .setBalance(BigDecimal.valueOf(100));

        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(bankAccountRepository.save(any())).thenReturn(account);

        BankAccount result = bankAccountService.withdraw(1L, BigDecimal.valueOf(75));

        assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(25));
    }

    @Test
    void should_throw_exception_for_withdraw_when_amount_negative() {
        BigDecimal negativeAmount = BigDecimal.valueOf(-200);
        assertThrows(NegativeNumberException.class, () ->
                bankAccountService.withdraw(1L, negativeAmount)
        );
    }

    @Test
    void should_throw_exception_for_deposit_when_amount_negative() {
        BigDecimal negativeAmount = BigDecimal.valueOf(-300);
        assertThrows(NegativeNumberException.class, () ->
                bankAccountService.deposit(1L, negativeAmount)
        );
    }


    @Test
    void should_throw_exception_when_amount_null() {
        assertThrows(CantBeNullException.class, () ->
                bankAccountService.withdraw(1L, null)
        );
    }

    @Test
    void should_throw_exception_when_amount_zero() {
        BigDecimal amount = BigDecimal.valueOf(0);
        assertThrows(CantBeZeroException.class, () ->
                bankAccountService.withdraw(1L, amount)
        );
    }

    @Test
    void should_throw_exception_when_account_not_found() {
        when(bankAccountRepository.findById(4L)).thenReturn(Optional.empty());
        BigDecimal amount = BigDecimal.valueOf(300);
        assertThrows(BankAccountNotFoundException.class, () ->
                bankAccountService.withdraw(4L, amount)
        );
    }


    @Test
    void should_throw_exception_when_insufficient_funds() {
        BigDecimal amount = BigDecimal.valueOf(500);
        BankAccount account = new BankAccount()
                .setId(1L)
                .setBalance(BigDecimal.valueOf(100));

        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(account));

        assertThrows(InsufficientFundsException.class, () ->
                bankAccountService.withdraw(1L, amount)
        );
    }

    @Test
    void should_create_account_successfully() {
        when(bankAccountRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        BankAccount result = bankAccountService.create();

        assertThat(result.getAccountNumber()).isNotNull();
        assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void should_throw_exception_when_overdraft_requested_on_savings() {
        SavingsAccount account = new SavingsAccount();
        account.setId(1L);
        BigDecimal overdraftAmount = BigDecimal.valueOf(200);

        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(account));

        assertThrows(OverdraftNotAllowedException.class, () ->
                bankAccountService.updateOverdraft(1L, overdraftAmount)
        );
    }

    @Test
    void should_return_empty_list_if_no_operations() {
        SavingsAccount account = new SavingsAccount();
        account.setId(1L);

        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(operationService.findByAccountId(1L)).thenReturn(Collections.emptyList());

        BankStatementDto result = bankAccountService.getStatement(1L);

        assertThat(result.getOperationDtos()).isEmpty();
    }

    @Test
    void should_return_correct_bank_type_for_statement_of_savings_account() {
        SavingsAccount account = new SavingsAccount();
        account.setId(1L);
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(operationService.findByAccountId(1L)).thenReturn(Collections.emptyList());

        BankStatementDto result = bankAccountService.getStatement(1L);

        assertThat(result.getAccountType()).isEqualTo(AccountType.SAVINGS.getLabel());
    }

    @Test
    void should_return_correct_bank_type_for_statement_of_bank_account() {
        BankAccount account = new BankAccount();
        account.setId(1L);
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(operationService.findByAccountId(1L)).thenReturn(Collections.emptyList());

        BankStatementDto result = bankAccountService.getStatement(1L);

        assertThat(result.getAccountType()).isEqualTo(AccountType.CURRENT.getLabel());
    }

    @Test
    void should_return_data_within_last_30_days_for_statement_of_savings_account() {
        BankAccount account = new BankAccount();
        account.setId(1L);
        Operation oldOperation = new Operation();
        oldOperation.setDate(LocalDateTime.now().minusMonths(2));
        oldOperation.setAmount(BigDecimal.valueOf(100));
        oldOperation.setType(OperationType.DEPOSIT);

        Operation recentOperation = new Operation();
        recentOperation.setDate(LocalDateTime.now().minusDays(10));
        recentOperation.setAmount(BigDecimal.valueOf(50));
        recentOperation.setType(OperationType.WITHDRAWAL);

        List<Operation> operations = List.of(oldOperation, recentOperation);

        when(operationService.findByAccountId(1L)).thenReturn(operations);
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(account));

        BankStatementDto result = bankAccountService.getStatement(1L);

        assertThat(result.getOperationDtos()).hasSize(1);
        assertThat(result.getOperationDtos().get(0).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(50));
    }

}
