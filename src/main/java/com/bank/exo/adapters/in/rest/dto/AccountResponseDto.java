package com.bank.exo.adapters.in.rest.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class AccountResponseDto {
    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private BigDecimal overdraftLimit;
    private BigDecimal depositLimit;
    private String accountType;
}
