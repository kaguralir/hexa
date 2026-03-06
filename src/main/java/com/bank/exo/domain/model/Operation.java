package com.bank.exo.domain.model;

import com.bank.exo.domain.OperationType;
import com.bank.exo.domain.valueobject.Amount;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Operation {
    private Long id;
    private Long accountId;
    private Amount amount;
    private OperationType type;
    private LocalDateTime date;
}
