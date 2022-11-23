package com.harington.kata.bank.api;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.harington.kata.bank.entity.dto.AccountDto;
import com.harington.kata.bank.entity.dto.AccountRequest;
import com.harington.kata.bank.formatters.AmountFormatter;
import com.harington.kata.bank.formatters.DatesFormatter;
import com.harington.kata.bank.service.AccountService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountResourceControllerTest {
    final static String API_BASE_URL = "/api/v1/accounts/";

    @MockBean
    AccountService accountService;
    @Autowired
    MockMvc mockMvc;

    @Test
    public void should_return_empty_list() throws Exception {
        Mockito.when(accountService.getAllAccounts()).thenReturn(List.of());
        mockMvc.perform(get(API_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void should_return_one_element() throws Exception {
        final String accNumber = UUID.randomUUID().toString();
        final String createdAt = DatesFormatter.format(LocalDateTime.now().minusMonths(1));
        Mockito.when(accountService.getAllAccounts()).thenReturn(List.of(
                AccountDto.builder()
                        .accountNumber(accNumber)
                        .currentBalance("100.00€")
                        .ownerName("NOUNI EL Bachir")
                        .createdAt(createdAt)
                        .build()
        ));
        mockMvc.perform(get(API_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].accountNumber", is(accNumber)))
                .andExpect(jsonPath("$[0].currentBalance", is("100.00€")))
                .andExpect(jsonPath("$[0].ownerName", is("NOUNI EL Bachir")))
                .andExpect(jsonPath("$[0].createdAt", is(createdAt)));
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
        final String accNumber = UUID.randomUUID().toString();
        final String createdAt = DatesFormatter.format(LocalDateTime.now().minusMonths(1));
        AccountRequest request = AccountRequest.builder()
                .initialBalanceInCents(100_00)
                .ownerName("NOUNI EL Bachir")
                .build();

        Mockito.when(accountService.createNewAccount(Mockito.anyString(), Mockito.anyInt()))
                .thenReturn(AccountDto.builder()
                        .accountNumber(accNumber)
                        .ownerName(request.getOwnerName())
                        .currentBalance(AmountFormatter.formatCents(request.getInitialBalanceInCents()))
                        .createdAt(createdAt)
                        .build());

        mockMvc.perform(post(API_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content((new JsonMapper()).writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.LOCATION, "/api/v1/accounts/" + accNumber))

                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.accountNumber", is(accNumber)))
                .andExpect(jsonPath("$.currentBalance", is("100.00€")))
                .andExpect(jsonPath("$.ownerName", is("NOUNI EL Bachir")))
                .andExpect(jsonPath("$.createdAt", is(createdAt)));
    }

    @Test
    public void should_return_not_found_when_no_existing_account_was_requested() throws Exception {
        UUID accountNumber = UUID.randomUUID();
        Mockito.when(accountService.findByAccountNumber(Mockito.any()))
                .thenReturn(Optional.empty());

        mockMvc.perform(get(API_BASE_URL + accountNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_return_account_when_existing_account_xas_requested() throws Exception {
        UUID accountNumber = UUID.randomUUID();
        AccountDto expectedAccount = AccountDto.builder()
                .accountNumber(accountNumber.toString())
                .currentBalance(AmountFormatter.formatCents(1000_00))//1000€
                .ownerName("Anis BESSA")
                .createdAt(DatesFormatter.format(LocalDateTime.of(2021, 1, 2, 10, 30, 0)))
                .build();
        Mockito.when(accountService.findByAccountNumber(Mockito.any()))
                .thenReturn(Optional.of(expectedAccount));

        mockMvc.perform(get(API_BASE_URL + accountNumber)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.accountNumber", is(accountNumber.toString())))
                .andExpect(jsonPath("$.currentBalance", is("1000.00€")))
                .andExpect(jsonPath("$.ownerName", is("Anis BESSA")))
                .andExpect(jsonPath("$.createdAt", is("02/01/2021 10:30:00")));
    }
}
