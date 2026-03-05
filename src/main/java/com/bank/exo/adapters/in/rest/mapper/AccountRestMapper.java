package com.bank.exo.adapters.in.rest.mapper;

import com.bank.exo.adapters.in.rest.dto.AccountResponseDto;
import com.bank.exo.domain.model.AbstractBankAccount;
import com.bank.exo.domain.model.SavingsAccount;
import org.springframework.stereotype.Component;

@Component
public class AccountRestMapper {

    public AccountResponseDto toDto(AbstractBankAccount account) {
        // Changement: on expose un DTO dédié pour éviter de renvoyer le modèle de domaine brut depuis l'API.
        AccountResponseDto.AccountResponseDtoBuilder builder = AccountResponseDto.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .overdraftLimit(account.getOverdraftLimit())
                .accountType(account.accountTypeLabel());

        if (account instanceof SavingsAccount savings) {
            builder.depositLimit(savings.getDepositLimit());
        }

        return builder.build();
    }
}
