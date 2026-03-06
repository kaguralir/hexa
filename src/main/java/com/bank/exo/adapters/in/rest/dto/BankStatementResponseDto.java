package com.bank.exo.adapters.in.rest.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class BankStatementResponseDto {
    private String accountType;
    private BigDecimal balance;
    private LocalDateTime emissionDate;
    private List<OperationResponseDto> operations;
}
