package com.bank.exo.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@DiscriminatorValue("SAVINGS")
public class SavingsAccount extends BankAccount {

    private BigDecimal depositLimit;

}
