package com.bank.exo.repository;

import com.bank.exo.model.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OperationRepository extends JpaRepository<Operation, Long> {
    List<Operation> findByBankAccountId(Long id);
}
