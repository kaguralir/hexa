package com.bank.exo.application.service;

import com.bank.exo.application.port.out.OperationRepositoryPort;
import com.bank.exo.domain.OperationType;
import com.bank.exo.domain.model.Operation;
import com.bank.exo.domain.valueobject.Amount;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Application service responsible for journaling account operations.
 * Composed into use cases instead of inherited from a base class.
 */
@Component
public class OperationRecorder {

    private final OperationRepositoryPort operationRepository;

    public OperationRecorder(OperationRepositoryPort operationRepository) {
        this.operationRepository = operationRepository;
    }

    public void record(Long accountId, Amount amount, OperationType type) {
        operationRepository.save(Operation.builder()
                .accountId(accountId)
                .amount(amount)
                .type(type)
                .date(LocalDateTime.now())
                .build());
    }

    public OperationRepositoryPort repository() {
        return operationRepository;
    }
}
