package com.bank.exo.api.controller;

import com.bank.exo.adapters.in.rest.dto.AccountResponseDto;
import com.bank.exo.adapters.in.rest.mapper.AccountRestMapper;
import com.bank.exo.api.dto.AmountDto;
import com.bank.exo.application.port.in.SavingsAccountUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/savings-accounts")
public class SavingsAccountController {

    private final SavingsAccountUseCase savingsAccountUseCase;
    private final AccountRestMapper accountRestMapper;

    @PostMapping
    @ManagedOperation(description = "Create a new bank account")
    public ResponseEntity<AccountResponseDto> createAccount() {
        return ResponseEntity.ok(accountRestMapper.toDto(savingsAccountUseCase.create()));
    }

    @PostMapping("/{id}/deposit")
    @ManagedOperation(description = "Deposit an amount into a saving bank account")
    public ResponseEntity<AccountResponseDto> deposit(@PathVariable Long id, @RequestBody AmountDto dto) {
        return ResponseEntity.ok(accountRestMapper.toDto(savingsAccountUseCase.deposit(id, dto.getValue())));
    }

    @PostMapping("/{id}/withdraw")
    @ManagedOperation(description = "Withdraw an amount from a saving bank account")
    public ResponseEntity<AccountResponseDto> withdraw(@PathVariable Long id, @RequestBody AmountDto dto) {
        return ResponseEntity.ok(accountRestMapper.toDto(savingsAccountUseCase.withdraw(id, dto.getValue())));
    }
}
