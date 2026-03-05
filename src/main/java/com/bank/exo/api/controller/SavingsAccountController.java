package com.bank.exo.api.controller;

import com.bank.exo.api.dto.AmountDto;
import com.bank.exo.model.BankAccount;
import com.bank.exo.service.impl.SavingsAccountServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/savings-accounts")
public class SavingsAccountController {

    private final SavingsAccountServiceImpl savingsAccountService;


    @PostMapping
    @ManagedOperation(description = "Create a new bank account")
    public ResponseEntity<BankAccount> createAccount() {
        return ResponseEntity.ok(savingsAccountService.create());
    }

    @PostMapping("/{id}/deposit")
    @ManagedOperation(description = "Deposit an amount into a saving bank account")
    public ResponseEntity<BankAccount> deposit(@PathVariable Long id, @RequestBody AmountDto dto) {
        return ResponseEntity.ok(savingsAccountService.deposit(id, dto.getValue()));
    }

    @PostMapping("/{id}/withdraw")
    @ManagedOperation(description = "Withdraw an amount from a saving bank account")
    public ResponseEntity<BankAccount> withdraw(@PathVariable Long id, @RequestBody AmountDto dto) {
        return ResponseEntity.ok(savingsAccountService.withdraw(id, dto.getValue()));
    }

}
