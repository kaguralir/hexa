package com.bank.exo.adapters.in.rest.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class OperationResponseDto {
    private String type;
    private BigDecimal amount;
    private LocalDateTime date;
}
