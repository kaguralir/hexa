package com.bank.exo;

import com.bank.exo.application.port.out.AccountRepositoryPort;
import com.bank.exo.application.port.out.OperationRepositoryPort;
import com.bank.exo.constant.OperationType;
import com.bank.exo.domain.model.CurrentAccount;
import com.bank.exo.domain.model.Operation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
    private AccountRepositoryPort accountRepository;

    @Autowired
    private OperationRepositoryPort operationRepository;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
        operationRepository.deleteAll();
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
        CurrentAccount account = new CurrentAccount(null, UUID.randomUUID().toString(), BigDecimal.ZERO, null);
        account = (CurrentAccount) accountRepository.save(account);

        mockMvc.perform(post("/api/accounts/" + account.getId() + "/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\": 100}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(100));
    }

    @Test
    void should_get_statement_successfully() throws Exception {
        CurrentAccount account = new CurrentAccount(null, UUID.randomUUID().toString(), BigDecimal.valueOf(591.02), null);
        account = (CurrentAccount) accountRepository.save(account);

        operationRepository.save(Operation.builder()
                .accountId(account.getId())
                .amount(BigDecimal.valueOf(500.50))
                .type(OperationType.DEPOSIT)
                .date(LocalDateTime.now().minusDays(15))
                .build());

        operationRepository.save(Operation.builder()
                .accountId(account.getId())
                .amount(BigDecimal.valueOf(90.52))
                .type(OperationType.DEPOSIT)
                .date(LocalDateTime.now().minusDays(2))
                .build());

        mockMvc.perform(get("/api/accounts/" + account.getId() + "/statement"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(591.02))
                .andExpect(jsonPath("$.operationDtos.length()").value(2))
                .andExpect(jsonPath("$.operationDtos[0].amount").value(90.52));
    }
}
