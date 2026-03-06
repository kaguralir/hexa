package com.bank.exo.adapters.in.rest.controller;

import com.bank.exo.adapters.in.rest.dto.AccountResponseDto;
import com.bank.exo.adapters.in.rest.dto.AmountDto;
import com.bank.exo.adapters.in.rest.dto.BankStatementResponseDto;
import com.bank.exo.adapters.in.rest.mapper.AccountRestMapper;
import com.bank.exo.adapters.in.rest.mapper.StatementRestMapper;
import com.bank.exo.application.port.in.CurrentAccountUseCase;
import com.bank.exo.domain.valueobject.Amount;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/accounts")
public class BankAccountController {

    private final CurrentAccountUseCase currentAccountUseCase;
    private final AccountRestMapper accountRestMapper;
    private final StatementRestMapper statementRestMapper;

    @PostMapping
    public ResponseEntity<AccountResponseDto> createAccount() {
        return ResponseEntity.ok(accountRestMapper.toDto(currentAccountUseCase.create()));
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<AccountResponseDto> deposit(@PathVariable Long id, @RequestBody AmountDto dto) {
        return ResponseEntity.ok(accountRestMapper.toDto(
                currentAccountUseCase.deposit(id, Amount.of(dto.getValue()))));
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<AccountResponseDto> withdraw(@PathVariable Long id, @RequestBody AmountDto dto) {
        return ResponseEntity.ok(accountRestMapper.toDto(
                currentAccountUseCase.withdraw(id, Amount.of(dto.getValue()))));
    }

    @PatchMapping("/{id}/overdraft")
    public ResponseEntity<AccountResponseDto> updateOverdraft(@PathVariable Long id, @RequestBody AmountDto dto) {
        return ResponseEntity.ok(accountRestMapper.toDto(
                currentAccountUseCase.updateOverdraft(id, dto.getValue())));
    }

    @GetMapping("/{id}/statement")
    public ResponseEntity<BankStatementResponseDto> getStatement(@PathVariable Long id) {
        return ResponseEntity.ok(statementRestMapper.toDto(currentAccountUseCase.getStatement(id)));
    }
}
