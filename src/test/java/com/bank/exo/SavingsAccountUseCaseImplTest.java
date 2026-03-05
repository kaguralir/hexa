package com.bank.exo;

import com.bank.exo.application.port.out.AccountRepositoryPort;
import com.bank.exo.application.port.out.OperationRepositoryPort;
import com.bank.exo.application.usecase.SavingsAccountUseCaseImpl;
import com.bank.exo.constant.OperationType;
import com.bank.exo.domain.model.Operation;
import com.bank.exo.domain.model.SavingsAccount;
import com.bank.exo.exception.InsufficientFundsException;
import com.bank.exo.exception.SavingsAccountLimitException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires du cas d'usage épargne.
 *
 * Philosophie "senior puriste":
 * - 1 test = 1 règle métier explicite.
 * - On vérifie aussi les effets de bord vers les ports sortants (journalisation).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SavingsAccountUseCaseImpl - tests unitaires")
class SavingsAccountUseCaseImplTest {

    @Mock
    private AccountRepositoryPort accountRepository;

    @Mock
    private OperationRepositoryPort operationRepository;

    @InjectMocks
    private SavingsAccountUseCaseImpl useCase;

    @Nested
    @DisplayName("Dépôt")
    class Deposit {

        @Test
        @DisplayName("refuse le dépôt au-delà du plafond d'épargne")
        void should_throw_exception_when_limit_exceeded() {
            SavingsAccount account = new SavingsAccount(1L, "S1", BigDecimal.valueOf(22940), BigDecimal.valueOf(22950));
            when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

            assertThatThrownBy(() -> useCase.deposit(1L, BigDecimal.valueOf(20)))
                    .isInstanceOf(SavingsAccountLimitException.class);
        }
    }

    @Nested
    @DisplayName("Retrait")
    class Withdraw {

        @Test
        @DisplayName("autorise le retrait si le solde couvre le montant")
        void should_withdraw_when_funds_sufficient() {
            SavingsAccount account = new SavingsAccount(1L, "S1", BigDecimal.valueOf(300), BigDecimal.valueOf(22950));
            when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
            when(accountRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            var result = useCase.withdraw(1L, BigDecimal.valueOf(100));

            assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(200));

            // Vérifie l'audit métier : un retrait doit toujours produire une opération.
            ArgumentCaptor<Operation> operationCaptor = ArgumentCaptor.forClass(Operation.class);
            verify(operationRepository).save(operationCaptor.capture());
            assertThat(operationCaptor.getValue().getType()).isEqualTo(OperationType.WITHDRAWAL);
            assertThat(operationCaptor.getValue().getAmount()).isEqualByComparingTo(BigDecimal.valueOf(100));
        }

        @Test
        @DisplayName("refuse le retrait si fonds insuffisants")
        void should_throw_exception_when_funds_insufficient() {
            SavingsAccount account = new SavingsAccount(1L, "S1", BigDecimal.valueOf(50), BigDecimal.valueOf(22950));
            when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

            assertThatThrownBy(() -> useCase.withdraw(1L, BigDecimal.valueOf(100)))
                    .isInstanceOf(InsufficientFundsException.class);
        }
    }
}
