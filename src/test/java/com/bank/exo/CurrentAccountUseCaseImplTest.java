package com.bank.exo;

import com.bank.exo.api.dto.BankStatementDto;
import com.bank.exo.application.mapper.StatementMapper;
import com.bank.exo.application.port.out.AccountRepositoryPort;
import com.bank.exo.application.port.out.OperationRepositoryPort;
import com.bank.exo.application.usecase.CurrentAccountUseCaseImpl;
import com.bank.exo.domain.model.CurrentAccount;
import com.bank.exo.exception.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrentAccountUseCaseImplTest {

    @Mock
    private AccountRepositoryPort accountRepository;

    @Mock
    private OperationRepositoryPort operationRepository;

    @Mock
    private StatementMapper statementMapper;

    @InjectMocks
    private CurrentAccountUseCaseImpl useCase;

    @Test
    void should_deposit_money_successfully_when_amount_positive() {
        CurrentAccount account = new CurrentAccount(1L, "A1", BigDecimal.valueOf(100), null);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = useCase.deposit(1L, BigDecimal.valueOf(50));

        assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(150));
    }

    @Test
    void should_throw_exception_when_insufficient_funds() {
        CurrentAccount account = new CurrentAccount(1L, "A1", BigDecimal.valueOf(100), null);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        assertThrows(InsufficientFundsException.class, () -> useCase.withdraw(1L, BigDecimal.valueOf(500)));
    }

    @Test
    void should_throw_exception_when_overdraft_limit_exceeded() {
        CurrentAccount account = new CurrentAccount(1L, "A1", BigDecimal.valueOf(100), BigDecimal.valueOf(200));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        assertThrows(OverdraftLimitException.class, () -> useCase.withdraw(1L, BigDecimal.valueOf(400)));
    }

    @Test
    void should_throw_exception_when_amount_invalid() {
        assertThrows(CantBeNullException.class, () -> useCase.deposit(1L, null));
        assertThrows(CantBeZeroException.class, () -> useCase.deposit(1L, BigDecimal.ZERO));
        assertThrows(NegativeNumberException.class, () -> useCase.deposit(1L, BigDecimal.valueOf(-1)));
    }

    @Test
    void should_get_statement() {
        CurrentAccount account = new CurrentAccount(1L, "A1", BigDecimal.TEN, null);
        BankStatementDto dto = new BankStatementDto();

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(operationRepository.findByAccountId(1L)).thenReturn(Collections.emptyList());
        when(statementMapper.toDto(account, Collections.emptyList())).thenReturn(dto);

        assertThat(useCase.getStatement(1L)).isSameAs(dto);
    }
}
