package com.bank.exo.adapters.in.rest.mapper;

import com.bank.exo.adapters.in.rest.dto.BankStatementResponseDto;
import com.bank.exo.adapters.in.rest.dto.OperationResponseDto;
import com.bank.exo.application.result.OperationResult;
import com.bank.exo.application.result.StatementResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StatementRestMapper {

    public BankStatementResponseDto toDto(StatementResult result) {
        BankStatementResponseDto dto = new BankStatementResponseDto();
        dto.setAccountType(result.accountType());
        dto.setBalance(result.balance());
        dto.setEmissionDate(result.emissionDate());
        dto.setOperations(toOperationDtos(result.operations()));
        return dto;
    }

    private List<OperationResponseDto> toOperationDtos(List<OperationResult> results) {
        return results.stream().map(this::toOperationDto).toList();
    }

    private OperationResponseDto toOperationDto(OperationResult result) {
        OperationResponseDto dto = new OperationResponseDto();
        dto.setType(result.operationType());
        dto.setAmount(result.amount());
        dto.setDate(result.date());
        return dto;
    }
}
