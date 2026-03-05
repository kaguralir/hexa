package com.bank.exo.adapters.out.memory;

import com.bank.exo.application.port.out.OperationRepositoryPort;
import com.bank.exo.domain.model.Operation;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryOperationRepositoryAdapter implements OperationRepositoryPort {

    private final AtomicLong sequence = new AtomicLong(0);
    private final List<Operation> operations = new CopyOnWriteArrayList<>();

    @Override
    public Operation save(Operation operation) {
        Operation stored = Operation.builder()
                .id(sequence.incrementAndGet())
                .accountId(operation.getAccountId())
                .amount(operation.getAmount())
                .type(operation.getType())
                .date(operation.getDate())
                .build();
        operations.add(stored);
        return stored;
    }

    @Override
    public List<Operation> findByAccountId(Long accountId) {
        return operations.stream().filter(op -> op.getAccountId().equals(accountId)).toList();
    }

    @Override
    public void deleteAll() {
        operations.clear();
        sequence.set(0);
    }
}
