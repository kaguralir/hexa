package com.bank.exo.application.mapper;

import com.bank.exo.application.result.OperationResult;
import com.bank.exo.application.result.StatementResult;
import com.bank.exo.domain.model.AbstractBankAccount;
import com.bank.exo.domain.model.Operation;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Component
public class StatementMapper {

    public StatementResult toResult(AbstractBankAccount account, List<Operation> operations) {
        List<OperationResult> operationResults = operations.stream()
                .sorted(Comparator.comparing(Operation::getDate).reversed())
                .map(this::toResult)
                .toList();

        return new StatementResult(
                account.accountTypeLabel(),
                account.getBalance(),
                LocalDateTime.now(),
                operationResults
        );
    }

    private OperationResult toResult(Operation operation) {
        return new OperationResult(
                operation.getType().getLabel(),
                operation.getAmount().value(),
                operation.getDate()
        );
    }
}
