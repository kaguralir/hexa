package com.bank.exo.api.controller;

import com.bank.exo.adapters.in.rest.dto.AccountResponseDto;
import com.bank.exo.adapters.in.rest.mapper.AccountRestMapper;
import com.bank.exo.api.dto.AmountDto;
import com.bank.exo.api.dto.BankStatementDto;
import com.bank.exo.application.port.in.CurrentAccountUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/accounts")
public class BankAccountController {

    private final CurrentAccountUseCase currentAccountUseCase;
    private final AccountRestMapper accountRestMapper;

    @PostMapping("/create")
    @ManagedOperation(description = "Create a new bank account")
    public ResponseEntity<AccountResponseDto> createAccount() {
        return ResponseEntity.ok(accountRestMapper.toDto(currentAccountUseCase.create()));
    }

    @PutMapping("/{id}/update")
    @ManagedOperation(description = "Update a bank account")
    public ResponseEntity<AccountResponseDto> updateAccount(@PathVariable Long id, @RequestBody AmountDto dto) {
        return ResponseEntity.ok(accountRestMapper.toDto(currentAccountUseCase.updateOverdraft(id, dto.getValue())));
    }

    @PostMapping("/{id}/deposit")
    @ManagedOperation(description = "Deposit an amount into a bank account")
    public ResponseEntity<AccountResponseDto> deposit(@PathVariable Long id, @RequestBody AmountDto dto) {
        return ResponseEntity.ok(accountRestMapper.toDto(currentAccountUseCase.deposit(id, dto.getValue())));
    }

    @PostMapping("/{id}/withdraw")
    @ManagedOperation(description = "Withdraw an amount from a bank account")
    public ResponseEntity<AccountResponseDto> withdraw(@PathVariable Long id, @RequestBody AmountDto dto) {
        return ResponseEntity.ok(accountRestMapper.toDto(currentAccountUseCase.withdraw(id, dto.getValue())));
    }

    @GetMapping("/{id}/statement")
    @ManagedOperation(description = "Get statement for a bank account")
    public ResponseEntity<BankStatementDto> getStatement(@PathVariable Long id) {
        return ResponseEntity.ok(currentAccountUseCase.getStatement(id));
    }
}
