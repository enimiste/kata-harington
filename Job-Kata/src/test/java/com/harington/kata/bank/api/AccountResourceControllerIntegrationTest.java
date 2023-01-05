package com.harington.kata.bank.api;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.harington.kata.bank.entity.Account;
import com.harington.kata.bank.entity.Transaction;
import com.harington.kata.bank.entity.dto.AccountDto;
import com.harington.kata.bank.entity.dto.AccountRequest;
import com.harington.kata.bank.formatters.AmountFormatter;
import com.harington.kata.bank.formatters.DatesFormatter;
import com.harington.kata.bank.repository.AccountRepository;
import com.harington.kata.bank.repository.TransactionRepository;
import com.harington.kata.bank.service.AccountService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountResourceControllerIntegrationTest {
    final static String API_BASE_URL = "/api/v1/accounts/";
    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    TransactionRepository transactionRepository;

    final static UUID accountNumber1 = UUID.randomUUID();
    final static UUID accountNumber2 = UUID.randomUUID();

    @BeforeEach
    void setup() {
        if (accountRepository.count() != 0) {
            transactionRepository.deleteAll();
            accountRepository.deleteAll();
        }
        //Account N° 1
        accountRepository.save(
                Account.builder()
                        .accountNumber(accountNumber1)
                        .initialBalanceInCents(100_00)
                        .currentBalanceInCents(100_00)
                        .ownerName("Anis BESSA")
                        .createdAt(LocalDateTime.now().minusMonths(1))
                        .build());
        //Account N° 2
        Account account2 = accountRepository.save(
                Account.builder()
                        .accountNumber(accountNumber2)
                        .initialBalanceInCents(0)
                        .currentBalanceInCents(100_00)
                        .ownerName("NOUNI EL Bachir")
                        .createdAt(LocalDateTime.now().minusMonths(1))
                        .build());
        List<Transaction> txs = List.of(
                Transaction.builder()
                        .txRef(UUID.randomUUID())
                        .account(account2)
                        .txType(Transaction.TxType.DEPOSIT)
                        .amountInCents(40_00)
                        .description("Depot d'argent 1")
                        .postTxAccountBalanceInCents(40_00)
                        .transactionAt(LocalDateTime.now().minusDays(15))
                        .build(),
                Transaction.builder()
                        .txRef(UUID.randomUUID())
                        .account(account2)
                        .txType(Transaction.TxType.DEPOSIT)
                        .amountInCents(80_00)
                        .description("Depot d'argent 2")
                        .postTxAccountBalanceInCents(120_00)
                        .transactionAt(LocalDateTime.now().minusDays(10))
                        .build(),
                Transaction.builder()
                        .txRef(UUID.randomUUID())
                        .account(account2)
                        .txType(Transaction.TxType.WITHDRAWAL)
                        .amountInCents(20_00)
                        .description("Withdrawal d'argent 1")
                        .postTxAccountBalanceInCents(100_00)
                        .transactionAt(LocalDateTime.now().minusDays(1))
                        .build()
        );
        txs.forEach(account2::addTx);
        transactionRepository.saveAll(txs);
    }

    @AfterEach
    void teardown() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    public void should_return_at_least_one_element() throws Exception {
        mockMvc.perform(get(API_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].accountNumber", is(accountNumber1.toString())))
                .andExpect(jsonPath("$[0].currentBalance", is("100.00€")))
                .andExpect(jsonPath("$[0].ownerName", is("Anis BESSA")));
    }

    @Test
    public void should_return_error_when_balance_negative() throws Exception {
        AccountRequest request = AccountRequest.builder()
                .initialBalanceInCents(-10)
                .ownerName("NOUNI EL Bachir")
                .build();

        mockMvc.perform(post(API_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content((new JsonMapper()).writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_return_error_when_owner_name_null_or_less_than_3_chars() throws Exception {
        //Null
        AccountRequest request = AccountRequest.builder()
                .initialBalanceInCents(10)
                .ownerName(null)
                .build();

        mockMvc.perform(post(API_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content((new JsonMapper()).writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        //Less than 3 chars
        AccountRequest request2 = AccountRequest.builder()
                .initialBalanceInCents(10)
                .ownerName("ab")
                .build();

        mockMvc.perform(post(API_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content((new JsonMapper()).writeValueAsString(request2)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_return_created_account() throws Exception {
        AccountRequest request = AccountRequest.builder()
                .initialBalanceInCents(100_00)
                .ownerName("NOUNI EL Bachir")
                .build();

        mockMvc.perform(post(API_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content((new JsonMapper()).writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.LOCATION, containsString("/api/v1/accounts/")))

                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.currentBalance", is("100.00€")))
                .andExpect(jsonPath("$.ownerName", is("NOUNI EL Bachir")));
    }

    @Test
    public void should_return_not_found_when_no_existing_account_was_requested() throws Exception {
        mockMvc.perform(get(API_BASE_URL + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_return_account_when_existing_account_xas_requested() throws Exception {
                mockMvc.perform(get(API_BASE_URL + accountNumber1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.accountNumber", is(accountNumber1.toString())))
                .andExpect(jsonPath("$.currentBalance", is("100.00€")))
                .andExpect(jsonPath("$.ownerName", is("Anis BESSA")));
    }
}
