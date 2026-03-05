package com.bank.exo.service;


import com.bank.exo.constant.OperationType;
import com.bank.exo.model.BankAccount;
import com.bank.exo.model.Operation;

import java.math.BigDecimal;
import java.util.List;

public interface OperationService {
    void saveOperation(BankAccount account, BigDecimal amount, OperationType type);

    List<Operation> findByAccountId(Long id);

}
