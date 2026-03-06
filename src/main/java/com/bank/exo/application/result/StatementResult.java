package com.bank.exo.application.result;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Application-layer output model for account statements.
 * The REST adapter maps this to its own presentation DTO.
 */
public record StatementResult(
        String accountType,
        BigDecimal balance,
        LocalDateTime emissionDate,
        List<OperationResult> operations) {
}
