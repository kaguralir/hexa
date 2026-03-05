package com.bank.exo;

import com.bank.exo.application.port.out.AccountRepositoryPort;
import com.bank.exo.application.port.out.OperationRepositoryPort;
import com.bank.exo.application.usecase.SavingsAccountUseCaseImpl;
import com.bank.exo.domain.model.SavingsAccount;
import com.bank.exo.exception.InsufficientFundsException;
import com.bank.exo.exception.SavingsAccountLimitException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SavingsAccountUseCaseImplTest {

    @Mock
    private AccountRepositoryPort accountRepository;

    @Mock
    private OperationRepositoryPort operationRepository;

    @InjectMocks
    private SavingsAccountUseCaseImpl useCase;

    @Test
    void should_throw_exception_when_limit_exceeded() {
        SavingsAccount account = new SavingsAccount(1L, "S1", BigDecimal.valueOf(22940), BigDecimal.valueOf(22950));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        assertThrows(SavingsAccountLimitException.class, () -> useCase.deposit(1L, BigDecimal.valueOf(20)));
    }

    @Test
    void should_withdraw_when_funds_sufficient() {
        SavingsAccount account = new SavingsAccount(1L, "S1", BigDecimal.valueOf(300), BigDecimal.valueOf(22950));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = useCase.withdraw(1L, BigDecimal.valueOf(100));
        assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(200));
    }

    @Test
    void should_throw_exception_when_funds_insufficient() {
        SavingsAccount account = new SavingsAccount(1L, "S1", BigDecimal.valueOf(50), BigDecimal.valueOf(22950));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        assertThrows(InsufficientFundsException.class, () -> useCase.withdraw(1L, BigDecimal.valueOf(100)));
    }
}
