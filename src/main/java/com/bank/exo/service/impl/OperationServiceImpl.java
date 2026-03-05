package com.bank.exo.service.impl;

import com.bank.exo.constant.OperationType;
import com.bank.exo.model.BankAccount;
import com.bank.exo.model.Operation;
import com.bank.exo.repository.OperationRepository;
import com.bank.exo.service.OperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class OperationServiceImpl implements OperationService {

    private final OperationRepository operationRepository;

    @Override
    public void saveOperation(BankAccount account, BigDecimal amount, OperationType type) {
        Operation operation = new Operation();
        operation.setBankAccount(account);
        operation.setAmount(amount);
        operation.setType(type);
        operation.setDate(LocalDateTime.now());
        operationRepository.save(operation);
    }

    @Override
    public List<Operation> findByAccountId(Long id) {
        return operationRepository.findByBankAccountId(id);
    }
}
