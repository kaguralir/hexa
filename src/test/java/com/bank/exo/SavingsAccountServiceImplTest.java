package com.bank.exo;

import com.bank.exo.exception.SavingsAccountLimitException;
import com.bank.exo.model.SavingsAccount;
import com.bank.exo.repository.SavingsAccountRepository;
import com.bank.exo.service.OperationService;
import com.bank.exo.service.impl.SavingsAccountServiceImpl;
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
class SavingsAccountServiceImplTest {

    @Mock
    private SavingsAccountRepository savingsAccountRepository;

    @InjectMocks
    private SavingsAccountServiceImpl
            savingsAccountService;

    @Mock
    private OperationService operationService;

    @Test
    void should_create_savings_account_successfully() {
        when(savingsAccountRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        SavingsAccount result = savingsAccountService.create();

        assertThat(result.getAccountNumber()).isNotNull();
        assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getDepositLimit()).isEqualByComparingTo(BigDecimal.valueOf(22950));
    }


    @Test
    void should_throw_exception_for_savings_account_if_deposit_exceeds_limit() {
        BigDecimal amount = BigDecimal.valueOf(100);

        SavingsAccount account = new SavingsAccount();
        account.setId(1L);
        account.setBalance(BigDecimal.valueOf(22949));
        account.setDepositLimit(BigDecimal.valueOf(22950));

        when(savingsAccountRepository.findById(1L)).thenReturn(Optional.of(account));
        assertThrows(SavingsAccountLimitException.class, () ->
                savingsAccountService.deposit(1L, amount)
        );
    }


    @Test
    void should_allow_deposit_for_savings_account_if_limit_not_exceeded() {
        BigDecimal amount = BigDecimal.valueOf(10);

        SavingsAccount account = new SavingsAccount();
        account.setId(1L);
        account.setBalance(BigDecimal.valueOf(22940));
        account.setDepositLimit(BigDecimal.valueOf(22950));
        when(savingsAccountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(savingsAccountRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        SavingsAccount result = savingsAccountService.deposit(1L, amount);

        assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(22950));

    }

}
