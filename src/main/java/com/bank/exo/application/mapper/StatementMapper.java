package com.bank.exo.application.mapper;

import com.bank.exo.api.dto.BankStatementDto;
import com.bank.exo.api.dto.OperationDto;
import com.bank.exo.domain.model.AbstractBankAccount;
import com.bank.exo.domain.model.Operation;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Component
public class StatementMapper {

    public BankStatementDto toDto(AbstractBankAccount account, List<Operation> operations) {
        List<OperationDto> operationDtos = operations.stream()
                .sorted(Comparator.comparing(Operation::getDate).reversed())
                .map(this::toDto)
                .toList();

        BankStatementDto statementDto = new BankStatementDto();
        statementDto.setOperationDtos(operationDtos);
        statementDto.setBalance(account.getBalance());
        statementDto.setEmissionDate(LocalDateTime.now());
        statementDto.setAccountType(account.accountTypeLabel());

        return statementDto;
    }

    private OperationDto toDto(Operation operation) {
        OperationDto dto = new OperationDto();
        dto.setType(operation.getType().getLabel());
        dto.setAmount(operation.getAmount());
        dto.setDate(operation.getDate());
        return dto;
    }
}
