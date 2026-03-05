package com.bank.exo.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class OperationDto {
    private String type;
    private BigDecimal amount;
    private LocalDateTime date;
}
