package com.bank.exo.api.controller;

import com.bank.exo.api.dto.AmountDto;
import com.bank.exo.api.dto.BankStatementDto;
import com.bank.exo.model.BankAccount;
import com.bank.exo.service.impl.BankAccountServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/accounts")
public class BankAccountController {

    private final BankAccountServiceImpl bankAccountService;


    @PostMapping("/create")
    @ManagedOperation(description = "Create a new bank account")
    public ResponseEntity<BankAccount> createAccount() {
        return ResponseEntity.ok(bankAccountService.create());
    }

    @PutMapping("/{id}/update")
    @ManagedOperation(description = "Update a bank account")
    public ResponseEntity<BankAccount> updateAccount(@PathVariable Long id, @RequestBody AmountDto dto) {
        return ResponseEntity.ok(bankAccountService.updateOverdraft(id, dto.getValue()));
    }

    @PostMapping("/{id}/deposit")
    @ManagedOperation(description = "Deposit an amount into a bank account")
    public ResponseEntity<BankAccount> deposit(@PathVariable Long id, @RequestBody AmountDto dto) {
        return ResponseEntity.ok(bankAccountService.deposit(id, dto.getValue()));
    }

    @PostMapping("/{id}/withdraw")
    @ManagedOperation(description = "Withdraw an amount from a bank account")
    public ResponseEntity<BankAccount> withdraw(@PathVariable Long id, @RequestBody AmountDto dto) {
        return ResponseEntity.ok(bankAccountService.withdraw(id, dto.getValue()));
    }

    @GetMapping("/{id}/statement")
    @ManagedOperation(description = "Get statement for a bank account")
    public ResponseEntity<BankStatementDto> getStatement(@PathVariable Long id) {
        return ResponseEntity.ok(bankAccountService.getStatement(id));
    }

}
