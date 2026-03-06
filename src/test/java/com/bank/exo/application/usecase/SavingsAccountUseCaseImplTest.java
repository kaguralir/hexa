package com.bank.exo.application.usecase;

import com.bank.exo.application.port.out.AccountRepositoryPort;
import com.bank.exo.application.port.out.OperationRepositoryPort;
import com.bank.exo.application.service.AccountLoader;
import com.bank.exo.application.service.OperationRecorder;
import com.bank.exo.domain.OperationType;
import com.bank.exo.domain.exception.DepositLimitExceededException;
import com.bank.exo.domain.exception.InsufficientFundsException;
import com.bank.exo.domain.model.Operation;
import com.bank.exo.domain.model.SavingsAccount;
import com.bank.exo.domain.valueobject.Amount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SavingsAccountUseCaseImpl - unit tests")
class SavingsAccountUseCaseImplTest {

    @Mock
    private AccountRepositoryPort accountRepository;

    @Mock
    private OperationRepositoryPort operationRepository;

    private SavingsAccountUseCaseImpl buildUseCase() {
        AccountLoader loader = new AccountLoader(accountRepository);
        OperationRecorder recorder = new OperationRecorder(operationRepository);
        return new SavingsAccountUseCaseImpl(accountRepository, loader, recorder);
    }

    @Nested
    @DisplayName("Deposit")
    class Deposit {

        @Test
        @DisplayName("rejects deposit that exceeds the savings ceiling")
        void should_throw_exception_when_limit_exceeded() {
            SavingsAccountUseCaseImpl useCase = buildUseCase();
            SavingsAccount account = new SavingsAccount(1L, "S1", BigDecimal.valueOf(22940), BigDecimal.valueOf(22950));
            when(accountRepository.findSavingsAccountById(1L)).thenReturn(Optional.of(account));

            assertThatThrownBy(() -> useCase.deposit(1L, Amount.of(BigDecimal.valueOf(20))))
                    .isInstanceOf(DepositLimitExceededException.class);
        }
    }

    @Nested
    @DisplayName("Withdraw")
    class Withdraw {

        @Test
        @DisplayName("withdraws when funds are sufficient and records the operation")
        void should_withdraw_when_funds_sufficient() {
            SavingsAccountUseCaseImpl useCase = buildUseCase();
            SavingsAccount account = new SavingsAccount(1L, "S1", BigDecimal.valueOf(300), BigDecimal.valueOf(22950));
            when(accountRepository.findSavingsAccountById(1L)).thenReturn(Optional.of(account));
            when(accountRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            var result = useCase.withdraw(1L, Amount.of(BigDecimal.valueOf(100)));

            assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(200));

            ArgumentCaptor<Operation> captor = ArgumentCaptor.forClass(Operation.class);
            verify(operationRepository).save(captor.capture());
            assertThat(captor.getValue().getType()).isEqualTo(OperationType.WITHDRAWAL);
            assertThat(captor.getValue().getAmount().value()).isEqualByComparingTo(BigDecimal.valueOf(100));
        }

        @Test
        @DisplayName("rejects withdrawal when funds are insufficient")
        void should_throw_exception_when_funds_insufficient() {
            SavingsAccountUseCaseImpl useCase = buildUseCase();
            SavingsAccount account = new SavingsAccount(1L, "S1", BigDecimal.valueOf(50), BigDecimal.valueOf(22950));
            when(accountRepository.findSavingsAccountById(1L)).thenReturn(Optional.of(account));

            assertThatThrownBy(() -> useCase.withdraw(1L, Amount.of(BigDecimal.valueOf(100))))
                    .isInstanceOf(InsufficientFundsException.class);
        }
    }
}
