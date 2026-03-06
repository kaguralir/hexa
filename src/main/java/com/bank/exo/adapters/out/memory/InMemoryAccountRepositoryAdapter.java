package com.bank.exo.adapters.out.memory;

import com.bank.exo.application.port.out.AccountRepositoryPort;
import com.bank.exo.domain.model.AbstractBankAccount;
import com.bank.exo.domain.model.SavingsAccount;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryAccountRepositoryAdapter implements AccountRepositoryPort {

    private final AtomicLong sequence = new AtomicLong(0);
    private final Map<Long, AbstractBankAccount> storage = new ConcurrentHashMap<>();

    @Override
    public AbstractBankAccount save(AbstractBankAccount account) {
        if (account.getId() == null) {
            account.assignId(sequence.incrementAndGet());
        }
        storage.put(account.getId(), account);
        return account;
    }

    @Override
    public Optional<AbstractBankAccount> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<SavingsAccount> findSavingsAccountById(Long id) {
        return Optional.ofNullable(storage.get(id))
                .filter(a -> a instanceof SavingsAccount)
                .map(a -> (SavingsAccount) a);
    }

    /** For test isolation only — not part of the domain port contract. */
    public void reset() {
        storage.clear();
        sequence.set(0);
    }
}
