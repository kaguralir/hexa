package com.bank.exo.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class BankStatementDto {
    private String accountType;
    private BigDecimal balance;
    private LocalDateTime emissionDate;
    private List<OperationDto> operationDtos;
}
