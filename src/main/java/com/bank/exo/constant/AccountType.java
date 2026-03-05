package com.bank.exo.constant;


import lombok.Getter;

@Getter
public enum AccountType {
    CURRENT("Compte Courant"),
    SAVINGS("Livret d'épargne");

    private final String label;

    AccountType(String label) {
        this.label = label;
    }

}
