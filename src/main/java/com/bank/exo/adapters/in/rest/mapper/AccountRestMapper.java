package com.bank.exo.adapters.in.rest.mapper;

import com.bank.exo.adapters.in.rest.dto.AccountResponseDto;
import com.bank.exo.domain.model.AbstractBankAccount;
import org.springframework.stereotype.Component;

@Component
public class AccountRestMapper {

    public AccountResponseDto toDto(AbstractBankAccount account) {
        return AccountResponseDto.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .overdraftLimit(account.getOverdraftLimit())
                .depositLimit(account.depositLimit().orElse(null))
                .accountType(account.accountTypeLabel())
                .build();
    }
}
