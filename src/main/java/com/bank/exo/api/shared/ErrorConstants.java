package com.bank.exo.api.shared;

public class ErrorConstants {

    public static final String INSUFFICIENT_FUNDS = "Insufficient funds";
    public static final String NEGATIVE_NUMBER = "Amount cannot be negative";
    public static final String CANT_BE_NULL = "Amount cannot be null";
    public static final String CANT_BE_ZERO = "Amount cannot be zero";
    public static final String ACCOUNT_NOT_FOUND = "Bank account not found";
    public static final String OVERDRAFT_EXCEEDED = "Amount exceeds overdraft limit";
    public static final String SAVINGS_ACCOUNT_LIMIT_EXCEEDED = "Deposit amount exceeds savings account limit";
    public static final String OVERDRAFT_NOT_ALLOWED = "Overdraft not allowed for this type of account";

    private ErrorConstants() {
    }
}
