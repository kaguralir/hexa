package com.bank.exo;

import com.bank.exo.api.dto.BankStatementDto;
import com.bank.exo.application.mapper.StatementMapper;
import com.bank.exo.application.port.out.AccountRepositoryPort;
import com.bank.exo.application.port.out.OperationRepositoryPort;
import com.bank.exo.application.usecase.CurrentAccountUseCaseImpl;
import com.bank.exo.constant.OperationType;
import com.bank.exo.domain.model.CurrentAccount;
import com.bank.exo.domain.model.Operation;
import com.bank.exo.exception.CantBeNullException;
import com.bank.exo.exception.CantBeZeroException;
import com.bank.exo.exception.InsufficientFundsException;
import com.bank.exo.exception.NegativeNumberException;
import com.bank.exo.exception.OverdraftLimitException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
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

/**
 * Tests unitaires orientés architecture hexagonale (port in + ports out mockés).
 *
 * Pourquoi ce niveau ?
 * - On valide les règles métier du cas d'usage indépendamment de Spring, HTTP ou la persistence.
 * - On vérifie explicitement le contrat avec les ports sortants (repo compte / repo opérations).
 * - C'est la couche la plus rapide à exécuter, donc idéale pour sécuriser les régressions.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CurrentAccountUseCaseImpl - tests unitaires du coeur applicatif")
class CurrentAccountUseCaseImplTest {

    @Mock
    private AccountRepositoryPort accountRepository;

    @Mock
    private OperationRepositoryPort operationRepository;

    @Mock
    private StatementMapper statementMapper;

    @InjectMocks
    private CurrentAccountUseCaseImpl useCase;

    @Nested
    @DisplayName("Dépôt")
    class Deposit {

        @Test
        @DisplayName("crédite le solde et journalise une opération de dépôt")
        void should_credit_balance_and_save_deposit_operation() {
            CurrentAccount account = new CurrentAccount(1L, "A1", BigDecimal.valueOf(100), null);
            when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
            when(accountRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            var result = useCase.deposit(1L, BigDecimal.valueOf(50));

            assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(150));

            // Ici on teste le contrat hexagonal: le use case doit publier une opération vers le port sortant.
            ArgumentCaptor<Operation> operationCaptor = ArgumentCaptor.forClass(Operation.class);
            verify(operationRepository).save(operationCaptor.capture());
            Operation operation = operationCaptor.getValue();
            assertThat(operation.getAccountId()).isEqualTo(1L);
            assertThat(operation.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(50));
            assertThat(operation.getType()).isEqualTo(OperationType.DEPOSIT);
            assertThat(operation.getDate()).isBeforeOrEqualTo(LocalDateTime.now());
        }

        @Test
        @DisplayName("rejette les montants invalides (null, zéro, négatif)")
        void should_reject_invalid_amounts() {
            assertThatThrownBy(() -> useCase.deposit(1L, null)).isInstanceOf(CantBeNullException.class);
            assertThatThrownBy(() -> useCase.deposit(1L, BigDecimal.ZERO)).isInstanceOf(CantBeZeroException.class);
            assertThatThrownBy(() -> useCase.deposit(1L, BigDecimal.valueOf(-1))).isInstanceOf(NegativeNumberException.class);
        }
    }

    @Nested
    @DisplayName("Retrait")
    class Withdraw {

        @Test
        @DisplayName("applique la règle de fonds insuffisants")
        void should_throw_when_insufficient_funds() {
            CurrentAccount account = new CurrentAccount(1L, "A1", BigDecimal.valueOf(100), null);
            when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

            assertThatThrownBy(() -> useCase.withdraw(1L, BigDecimal.valueOf(500)))
                    .isInstanceOf(InsufficientFundsException.class);
        }

        @Test
        @DisplayName("applique la règle de limite de découvert")
        void should_throw_when_overdraft_limit_exceeded() {
            CurrentAccount account = new CurrentAccount(1L, "A1", BigDecimal.valueOf(100), BigDecimal.valueOf(200));
            when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

            assertThatThrownBy(() -> useCase.withdraw(1L, BigDecimal.valueOf(400)))
                    .isInstanceOf(OverdraftLimitException.class);
        }
    }

    @Test
    @DisplayName("Statement: ne conserve que les opérations du dernier mois")
    void should_return_statement_with_last_month_operations_only() {
        CurrentAccount account = new CurrentAccount(1L, "A1", BigDecimal.TEN, null);
        BankStatementDto dto = new BankStatementDto();

        Operation recent = Operation.builder()
                .accountId(1L)
                .amount(BigDecimal.ONE)
                .type(OperationType.DEPOSIT)
                .date(LocalDateTime.now().minusDays(2))
                .build();

        Operation tooOld = Operation.builder()
                .accountId(1L)
                .amount(BigDecimal.TEN)
                .type(OperationType.DEPOSIT)
                .date(LocalDateTime.now().minusMonths(2))
                .build();

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(operationRepository.findByAccountId(1L)).thenReturn(List.of(recent, tooOld));
        when(statementMapper.toDto(account, List.of(recent))).thenReturn(dto);

        assertThat(useCase.getStatement(1L)).isSameAs(dto);

        // On vérifie la "couture" applicative: c'est bien la liste filtrée qui est envoyée au mapper.
        verify(statementMapper).toDto(account, List.of(recent));
    }
}
