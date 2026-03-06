package com.bank.exo.adapters.in.rest.shared;

import com.bank.exo.domain.exception.AccountNotFoundException;
import com.bank.exo.domain.exception.DepositLimitExceededException;
import com.bank.exo.domain.exception.InsufficientFundsException;
import com.bank.exo.domain.exception.InvalidAmountException;
import com.bank.exo.domain.exception.OverdraftLimitExceededException;
import com.bank.exo.domain.exception.OverdraftNotAllowedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(InvalidAmountException.class)
    public ProblemDetail handleInvalidAmount(InvalidAmountException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Invalid amount");
        return problem;
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ProblemDetail handleInsufficientFunds(InsufficientFundsException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Insufficient funds");
        return problem;
    }

    @ExceptionHandler(OverdraftLimitExceededException.class)
    public ProblemDetail handleOverdraftLimitExceeded(OverdraftLimitExceededException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Overdraft limit exceeded");
        return problem;
    }

    @ExceptionHandler(OverdraftNotAllowedException.class)
    public ProblemDetail handleOverdraftNotAllowed(OverdraftNotAllowedException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Overdraft not allowed");
        return problem;
    }

    @ExceptionHandler(DepositLimitExceededException.class)
    public ProblemDetail handleDepositLimitExceeded(DepositLimitExceededException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Deposit limit exceeded");
        return problem;
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ProblemDetail handleAccountNotFound(AccountNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Account not found");
        return problem;
    }
}
