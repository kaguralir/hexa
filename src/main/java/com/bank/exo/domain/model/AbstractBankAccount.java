package com.bank.exo.domain.model;

import com.bank.exo.domain.exception.OverdraftNotAllowedException;
import com.bank.exo.domain.valueobject.Amount;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

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

    /**
     * Called by the persistence adapter after ID generation.
     * Only takes effect once — subsequent calls are no-ops.
     */
    public void assignId(Long id) {
        if (this.id != null) {
            return;
        }
        this.id = id;
    }

    public void addToBalance(Amount amount) {
        this.balance = this.balance.add(amount.value());
    }

    public void subtractFromBalance(Amount amount) {
        this.balance = this.balance.subtract(amount.value());
    }

    public void updateOverdraftLimit(BigDecimal amount) {
        this.overdraftLimit = amount;
    }

    /**
     * Each account subtype carries its own deposit rules (OCP).
     * The use case delegates to the entity — no instanceof needed.
     */
    public abstract void assertCanDeposit(Amount amount);

    public abstract void assertCanWithdraw(Amount amount);

    /**
     * Default: overdraft updates are forbidden.
     * CurrentAccount overrides to allow it.
     */
    public void assertCanUpdateOverdraft() {
        throw new OverdraftNotAllowedException();
    }

    /**
     * Returns the deposit ceiling when the account type enforces one.
     */
    public Optional<BigDecimal> depositLimit() {
        return Optional.empty();
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
