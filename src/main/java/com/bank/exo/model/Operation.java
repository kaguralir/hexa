package com.bank.exo.model;

import com.bank.exo.constant.OperationType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Operation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;
    private OperationType type;
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "bank_account_id")
    @JsonIgnore
    private BankAccount bankAccount;
}
