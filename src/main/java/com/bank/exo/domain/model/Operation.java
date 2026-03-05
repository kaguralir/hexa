package com.bank.exo.domain.model;

import com.bank.exo.constant.OperationType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class Operation {
    private Long id;
    private Long accountId;
    private BigDecimal amount;
    private OperationType type;
    private LocalDateTime date;
}
