package com.bank.exo.api.shared;


import com.bank.exo.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@Slf4j
@ControllerAdvice
public class RestResponseEntityExceptionHandler {

    @ExceptionHandler(InsufficientFundsException.class)
    public ProblemDetail handleInsufficientFunds(InsufficientFundsException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle(ErrorConstants.INSUFFICIENT_FUNDS);
        return problem;
    }

    @ExceptionHandler(BankAccountNotFoundException.class)
    public ProblemDetail handleAccountNotFound(BankAccountNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle(ErrorConstants.ACCOUNT_NOT_FOUND);
        return problem;
    }

    @ExceptionHandler(NegativeNumberException.class)
    public ProblemDetail handleNegativeNumber(NegativeNumberException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle(ErrorConstants.NEGATIVE_NUMBER);
        return problem;
    }

    @ExceptionHandler(CantBeNullException.class)
    public ProblemDetail handleCantBeNull(CantBeNullException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle(ErrorConstants.CANT_BE_NULL);
        return problem;
    }

    @ExceptionHandler(CantBeZeroException.class)
    public ProblemDetail handleCantBeZero(CantBeZeroException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle(ErrorConstants.CANT_BE_ZERO);
        return problem;
    }

    @ExceptionHandler(OverdraftLimitException.class)
    public ProblemDetail handleOverdraftLimit(OverdraftLimitException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle(ErrorConstants.OVERDRAFT_EXCEEDED);
        return problem;
    }

    @ExceptionHandler(SavingsAccountLimitException.class)
    public ProblemDetail handleSavingsAccountLimitException(SavingsAccountLimitException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle(ErrorConstants.SAVINGS_ACCOUNT_LIMIT_EXCEEDED);
        return problem;
    }

    @ExceptionHandler(OverdraftNotAllowedException.class)
    public ProblemDetail handleOverdraftNotAllowedException(OverdraftNotAllowedException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle(ErrorConstants.OVERDRAFT_NOT_ALLOWED);
        return problem;
    }
}
