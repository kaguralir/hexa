package com.bank.exo.application.port.out;

import com.bank.exo.domain.model.Operation;

import java.util.List;

public interface OperationRepositoryPort {
    Operation save(Operation operation);

    List<Operation> findByAccountId(Long accountId);

}
