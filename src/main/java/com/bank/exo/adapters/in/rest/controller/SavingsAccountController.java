package com.bank.exo.adapters.in.rest.controller;

import com.bank.exo.adapters.in.rest.dto.AccountResponseDto;
import com.bank.exo.adapters.in.rest.dto.AmountDto;
import com.bank.exo.adapters.in.rest.mapper.AccountRestMapper;
import com.bank.exo.application.port.in.SavingsAccountUseCase;
import com.bank.exo.domain.valueobject.Amount;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/savings-accounts")
public class SavingsAccountController {

    private final SavingsAccountUseCase savingsAccountUseCase;
    private final AccountRestMapper accountRestMapper;

    @PostMapping
    public ResponseEntity<AccountResponseDto> createAccount() {
        return ResponseEntity.ok(accountRestMapper.toDto(savingsAccountUseCase.create()));
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<AccountResponseDto> deposit(@PathVariable Long id, @RequestBody AmountDto dto) {
        return ResponseEntity.ok(accountRestMapper.toDto(
                savingsAccountUseCase.deposit(id, Amount.of(dto.getValue()))));
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<AccountResponseDto> withdraw(@PathVariable Long id, @RequestBody AmountDto dto) {
        return ResponseEntity.ok(accountRestMapper.toDto(
                savingsAccountUseCase.withdraw(id, Amount.of(dto.getValue()))));
    }
}
