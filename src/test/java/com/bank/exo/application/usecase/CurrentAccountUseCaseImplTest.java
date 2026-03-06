package com.bank.exo.application.usecase;

import com.bank.exo.application.mapper.StatementMapper;
import com.bank.exo.application.port.out.AccountRepositoryPort;
import com.bank.exo.application.port.out.OperationRepositoryPort;
import com.bank.exo.application.result.StatementResult;
import com.bank.exo.application.service.AccountLoader;
import com.bank.exo.application.service.OperationRecorder;
import com.bank.exo.domain.OperationType;
import com.bank.exo.domain.exception.InsufficientFundsException;
import com.bank.exo.domain.exception.InvalidAmountException;
import com.bank.exo.domain.exception.OverdraftLimitExceededException;
import com.bank.exo.domain.model.CurrentAccount;
import com.bank.exo.domain.model.Operation;
import com.bank.exo.domain.valueobject.Amount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CurrentAccountUseCaseImpl - unit tests")
class CurrentAccountUseCaseImplTest {

    @Mock
    private AccountRepositoryPort accountRepository;

    @Mock
    private OperationRepositoryPort operationRepository;

    @Mock
    private StatementMapper statementMapper;

    private CurrentAccountUseCaseImpl buildUseCase() {
        AccountLoader loader = new AccountLoader(accountRepository);
        OperationRecorder recorder = new OperationRecorder(operationRepository);
        return new CurrentAccountUseCaseImpl(accountRepository, loader, recorder, statementMapper);
    }

    @Nested
    @DisplayName("Deposit")
    class Deposit {

        @Test
        @DisplayName("credits balance and records a deposit operation")
        void should_credit_balance_and_save_deposit_operation() {
            CurrentAccountUseCaseImpl useCase = buildUseCase();
            CurrentAccount account = new CurrentAccount(1L, "A1", BigDecimal.valueOf(100), null);
            when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
            when(accountRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            var result = useCase.deposit(1L, Amount.of(BigDecimal.valueOf(50)));

            assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(150));

            ArgumentCaptor<Operation> captor = ArgumentCaptor.forClass(Operation.class);
            verify(operationRepository).save(captor.capture());
            Operation op = captor.getValue();
            assertThat(op.getAccountId()).isEqualTo(1L);
            assertThat(op.getAmount().value()).isEqualByComparingTo(BigDecimal.valueOf(50));
            assertThat(op.getType()).isEqualTo(OperationType.DEPOSIT);
            assertThat(op.getDate()).isBeforeOrEqualTo(LocalDateTime.now());
        }

        @Test
        @DisplayName("rejects null, zero, and negative amounts")
        void should_reject_invalid_amounts() {
            CurrentAccountUseCaseImpl useCase = buildUseCase();
            assertThatThrownBy(() -> Amount.of(null)).isInstanceOf(InvalidAmountException.class);
            assertThatThrownBy(() -> Amount.of(BigDecimal.ZERO)).isInstanceOf(InvalidAmountException.class);
            assertThatThrownBy(() -> Amount.of(BigDecimal.valueOf(-1))).isInstanceOf(InvalidAmountException.class);
        }
    }

    @Nested
    @DisplayName("Withdraw")
    class Withdraw {

        @Test
        @DisplayName("throws when insufficient funds")
        void should_throw_when_insufficient_funds() {
            CurrentAccountUseCaseImpl useCase = buildUseCase();
            CurrentAccount account = new CurrentAccount(1L, "A1", BigDecimal.valueOf(100), null);
            when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

            assertThatThrownBy(() -> useCase.withdraw(1L, Amount.of(BigDecimal.valueOf(500))))
                    .isInstanceOf(InsufficientFundsException.class);
        }

        @Test
        @DisplayName("throws when overdraft limit exceeded")
        void should_throw_when_overdraft_limit_exceeded() {
            CurrentAccountUseCaseImpl useCase = buildUseCase();
            CurrentAccount account = new CurrentAccount(1L, "A1", BigDecimal.valueOf(100), BigDecimal.valueOf(200));
            when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

            assertThatThrownBy(() -> useCase.withdraw(1L, Amount.of(BigDecimal.valueOf(400))))
                    .isInstanceOf(OverdraftLimitExceededException.class);
        }
    }

    @Test
    @DisplayName("Statement: returns only last-month operations")
    void should_return_statement_with_last_month_operations_only() {
        CurrentAccountUseCaseImpl useCase = buildUseCase();
        CurrentAccount account = new CurrentAccount(1L, "A1", BigDecimal.TEN, null);
        StatementResult result = new StatementResult("Compte Courant", BigDecimal.TEN, LocalDateTime.now(), List.of());

        Operation recent = Operation.builder()
                .accountId(1L)
                .amount(Amount.of(BigDecimal.ONE))
                .type(OperationType.DEPOSIT)
                .date(LocalDateTime.now().minusDays(2))
                .build();

        Operation tooOld = Operation.builder()
                .accountId(1L)
                .amount(Amount.of(BigDecimal.TEN))
                .type(OperationType.DEPOSIT)
                .date(LocalDateTime.now().minusMonths(2))
                .build();

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(operationRepository.findByAccountId(1L)).thenReturn(List.of(recent, tooOld));
        when(statementMapper.toResult(account, List.of(recent))).thenReturn(result);

        assertThat(useCase.getStatement(1L)).isSameAs(result);
        verify(statementMapper).toResult(account, List.of(recent));
    }
}
