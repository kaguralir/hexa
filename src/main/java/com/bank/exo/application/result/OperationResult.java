package com.bank.exo.application.result;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Application-layer output model for a single operation.
 * Decouples the application core from the REST presentation layer.
 */
public record OperationResult(String operationType, BigDecimal amount, LocalDateTime date) {
}
