package com.bank.exo;


import com.bank.exo.constant.OperationType;
import com.bank.exo.model.BankAccount;
import com.bank.exo.model.Operation;
import com.bank.exo.repository.BankAccountRepository;
import com.bank.exo.repository.OperationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class BankAccountIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private OperationRepository operationRepository;

    @BeforeEach
    void setUp() {
        bankAccountRepository.deleteAll();
    }

    @Test
    void should_create_account() throws Exception {
        mockMvc.perform(post("/api/accounts/create"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(0))
                .andExpect(jsonPath("$.accountNumber").isNotEmpty());
    }

    @Test
    void should_deposit_successfully() throws Exception {
        BankAccount account = bankAccountRepository.save(new BankAccount()
                .setAccountNumber(UUID.randomUUID().toString())
                .setBalance(BigDecimal.ZERO));

        mockMvc.perform(post("/api/accounts/" + account.getId() + "/deposit")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content("{\"value\": 100}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(100));
    }

    @Test
    void should_withdraw_successfully() throws Exception {
        BankAccount account = bankAccountRepository.save(new BankAccount()
                .setAccountNumber(UUID.randomUUID().toString())
                .setBalance(BigDecimal.valueOf(500)));

        mockMvc.perform(post("/api/accounts/" + account.getId() + "/withdraw")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content("{\"value\": 100}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(400));
    }

    @Test
    void should_update_overdraft_successfully() throws Exception {
        BankAccount account = bankAccountRepository.save(new BankAccount()
                .setAccountNumber(UUID.randomUUID().toString())
                .setBalance(BigDecimal.valueOf(500)));

        mockMvc.perform(put("/api/accounts/" + account.getId() + "/update")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content("{\"value\": 100}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.overdraftLimit").value(100));
    }


    @Test
    void should_throw_404_when_account_not_found() throws Exception {
        mockMvc.perform(post("/api/accounts/999/deposit")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content("{\"value\": 100}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_throw_400_when_amount_is_zero() throws Exception {
        BankAccount account = bankAccountRepository.save(new BankAccount()
                .setAccountNumber(UUID.randomUUID().toString())
                .setBalance(BigDecimal.ZERO));

        mockMvc.perform(post("/api/accounts/" + account.getId() + "/deposit")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content("{\"value\": 0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_get_statement_successfully() throws Exception {
        BankAccount account = bankAccountRepository.save(new BankAccount()
                .setAccountNumber(UUID.randomUUID().toString())
                .setBalance(BigDecimal.valueOf(591.02)));

        Operation operation = new Operation();
        operation.setBankAccount(account);
        operation.setAmount(BigDecimal.valueOf(500.50));
        operation.setType(OperationType.DEPOSIT);
        operation.setDate(LocalDateTime.now().minusDays(15));
        operationRepository.save(operation);

        Operation operation2 = new Operation();
        operation2.setBankAccount(account);
        operation2.setAmount(BigDecimal.valueOf(90.52));
        operation2.setType(OperationType.DEPOSIT);
        operation2.setDate(LocalDateTime.now().minusDays(2));
        operationRepository.save(operation2);
        mockMvc.perform(get("/api/accounts/" + account.getId() + "/statement"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(591.02))
                .andExpect(jsonPath("$.operationDtos.length()").value(2))
                .andExpect(jsonPath("$.operationDtos[0].amount").value(90.52));
    }

}
