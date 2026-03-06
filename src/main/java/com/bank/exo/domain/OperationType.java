package com.bank.exo.domain;

import lombok.Getter;

@Getter
public enum OperationType {
    DEPOSIT("Dépôt"),
    WITHDRAWAL("Retrait");

    private final String label;

    OperationType(String label) {
        this.label = label;
    }
}
