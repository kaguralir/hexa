package com.bank.exo.adapters.in.rest.mapper;

import com.bank.exo.adapters.in.rest.dto.AccountResponseDto;
import com.bank.exo.domain.model.AbstractBankAccount;
import org.springframework.stereotype.Component;

@Component
public class AccountRestMapper {

    public AccountResponseDto toDto(AbstractBankAccount account) {
        // Changement: on expose un DTO dédié pour éviter de renvoyer le modèle de domaine brut depuis l'API.
        // Changement: mapping piloté par un contrat polymorphique du domaine (sans instanceof).
        return AccountResponseDto.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .overdraftLimit(account.getOverdraftLimit())
                .depositLimit(account.depositLimitOrNull())
                .accountType(account.accountTypeLabel())
                .build();
    }
}
