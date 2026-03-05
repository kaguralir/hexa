package com.bank.exo.domain.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
public abstract class AbstractBankAccount {
    private Long id;
    private final String accountNumber;
    private BigDecimal balance;
    private BigDecimal overdraftLimit;

    protected AbstractBankAccount(Long id, String accountNumber, BigDecimal balance, BigDecimal overdraftLimit) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.overdraftLimit = overdraftLimit;
    }

    public void assignId(Long id) {
        if (this.id != null) {
            return;
        }
        this.id = id;
    }

    public void addToBalance(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void subtractFromBalance(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }

    public void updateOverdraftLimit(BigDecimal amount) {
        this.overdraftLimit = amount;
    }

    /**
     * Changement SOLID (OCP): chaque type de compte porte ses propres règles métier.
     * Le service applicatif n'a plus besoin de "instanceof" pour valider dépôt/retrait.
     */
    public abstract void assertCanDeposit(BigDecimal amount);

    public abstract void assertCanWithdraw(BigDecimal amount);

    /**
     * Changement API/mapper: contrat polymorphique pour exposer la limite de dépôt
     * uniquement quand le type de compte le supporte.
     */
    public BigDecimal depositLimitOrNull() {
        return null;
    }

    public abstract String accountTypeLabel();

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractBankAccount other)) {
            return false;
        }
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
