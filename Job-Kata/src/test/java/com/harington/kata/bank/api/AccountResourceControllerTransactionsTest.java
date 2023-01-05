package com.harington.kata.bank.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harington.kata.bank.entity.Transaction;
import com.harington.kata.bank.entity.dto.TransactionDto;
import com.harington.kata.bank.entity.dto.TransactionRequest;
import com.harington.kata.bank.exceptions.EntityNotFoundException;
import com.harington.kata.bank.exceptions.InvalidOperationException;
import com.harington.kata.bank.formatters.DatesFormatter;
import com.harington.kata.bank.service.TransactionService;
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
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountResourceControllerTransactionsTest {
        final static String API_BASE_URL = "/api/v1/accounts";
        final static String API_TX_BASE_URL = API_BASE_URL + "/transactions";
        final static String API_ACCOUNT_TX_BASE_URL = API_BASE_URL + "/%s/transactions";

        @Autowired
        MockMvc mockMvc;

        @MockBean
        TransactionService transactionService;

        @Test
        public void should_return_error_when_deposit_request_amount_is_negatif() throws Exception {
                UUID accountNumber = UUID.randomUUID();
                TransactionRequest request = TransactionRequest.builder()
                                .accountNumber(accountNumber)
                                .amountInCents(-100_00)
                                .operation(Transaction.TxType.DEPOSIT)
                                .description("Dépôt N° 1")
                                .build();

                mockMvc.perform(post(API_TX_BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content((new ObjectMapper()).writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        public void should_return_error_when_deposit_request_account_not_existing() throws Exception {
                UUID accountNumber = UUID.randomUUID();
                TransactionRequest request = TransactionRequest.builder()
                                .accountNumber(accountNumber)
                                .amountInCents(100_00)
                                .operation(Transaction.TxType.DEPOSIT)
                                .description("Dépôt N° 1")
                                .build();

                Mockito.when(transactionService.doDepositOn(Mockito.any(),
                                Mockito.anyInt(),
                                Mockito.anyString()))
                                .thenThrow(EntityNotFoundException.class);

                mockMvc.perform(post(API_TX_BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content((new ObjectMapper()).writeValueAsString(request)))
                                .andExpect(status().isNotFound());
        }

        @Test
        public void should_update_account_balance_when_do_deposit() throws Exception {
                UUID accountNumber = UUID.randomUUID();
                TransactionRequest request = TransactionRequest.builder()
                                .accountNumber(accountNumber)
                                .amountInCents(100_00)
                                .operation(Transaction.TxType.DEPOSIT)
                                .description("Dépôt N° 1")
                                .build();

                LocalDateTime txAt = LocalDateTime.now();
            TransactionDto dto = TransactionDto.builder()
                    .txRef(UUID.randomUUID().toString())
                    .accountBalance("200.00€")
                    .transactionAt(DatesFormatter.format(txAt))
                    .amount("100.00€")
                    .accountNumber(accountNumber.toString())
                    .description("Dépôt N° 1")
                    .operation(Transaction.TxType.DEPOSIT.name())
                    .build();

            Mockito.when(transactionService.doDepositOn(accountNumber,
                            100_00,
                            dto.getDescription()))
                    .thenReturn(dto);

            mockMvc.perform(post(API_TX_BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content((new ObjectMapper()).writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string(HttpHeaders.LOCATION,
                            "/api/v1/accounts/" + accountNumber + "/transactions"))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isNotEmpty())
                                .andExpect(jsonPath("$.txRef").isNotEmpty())
                                .andExpect(jsonPath("$.accountBalance", is(dto.getAccountBalance())))
                                .andExpect(jsonPath("$.amount", is(dto.getAmount())))
                                .andExpect(jsonPath("$.transactionAt", is(dto.getTransactionAt())))
                                .andExpect(jsonPath("$.description", is(dto.getDescription())))
                                .andExpect(jsonPath("$.operation", is(dto.getOperation())))
                                .andExpect(jsonPath("$.accountNumber", is(dto.getAccountNumber())));
        }

        @Test
        public void should_update_account_balance_when_do_withdrawal_all() throws Exception {
                UUID accountNumber = UUID.randomUUID();
                TransactionRequest request = TransactionRequest.builder()
                                .accountNumber(accountNumber)
                                .amountInCents(100_00)
                                .operation(Transaction.TxType.WITHDRAWAL)
                                .description("Withdrawal ALL")
                                .build();

                LocalDateTime txAt = LocalDateTime.now();
                TransactionDto dto = TransactionDto.builder()
                                .txRef(UUID.randomUUID().toString())
                                .accountBalance("00.00€")
                                .transactionAt(DatesFormatter.format(txAt))
                                .amount("100.00€")
                                .accountNumber(accountNumber.toString())
                                .description("Withdrawal ALL N° 1")
                                .operation(Transaction.TxType.WITHDRAWAL.name())
                                .build();

                Mockito.when(transactionService.doWithdrawalOn(Mockito.any(),
                                Mockito.anyInt(),
                                Mockito.anyString()))
                                .thenReturn(dto);

                mockMvc.perform(post(API_TX_BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content((new ObjectMapper()).writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(header().string(HttpHeaders.LOCATION,
                                                "/api/v1/accounts/" + accountNumber + "/transactions"))
                                .andExpect(jsonPath("$").isNotEmpty())
                                .andExpect(jsonPath("$.txRef").isNotEmpty())
                                .andExpect(jsonPath("$.accountBalance", is(dto.getAccountBalance())))
                                .andExpect(jsonPath("$.amount", is(dto.getAmount())))
                                .andExpect(jsonPath("$.transactionAt", is(dto.getTransactionAt())))
                                .andExpect(jsonPath("$.description", is(dto.getDescription())))
                                .andExpect(jsonPath("$.operation", is(dto.getOperation())))
                                .andExpect(jsonPath("$.accountNumber", is(dto.getAccountNumber())));
        }

        @Test
        public void should_update_account_balance_when_do_withdrawal_partial() throws Exception {
                UUID accountNumber = UUID.randomUUID();
                TransactionRequest request = TransactionRequest.builder()
                                .accountNumber(accountNumber)
                                .amountInCents(10_00)
                                .operation(Transaction.TxType.WITHDRAWAL)
                                .description("Withdrawal 10€")
                                .build();

                LocalDateTime txAt = LocalDateTime.now();
                TransactionDto dto = TransactionDto.builder()
                                .txRef(UUID.randomUUID().toString())
                                .accountBalance("90.00€")
                                .transactionAt(DatesFormatter.format(txAt))
                                .amount("10.00€")
                                .accountNumber(accountNumber.toString())
                                .description("Withdrawal 10€")
                                .operation(Transaction.TxType.WITHDRAWAL.name())
                                .build();

                Mockito.when(transactionService.doWithdrawalOn(Mockito.any(),
                                Mockito.anyInt(),
                                Mockito.anyString()))
                                .thenReturn(dto);

                mockMvc.perform(post(API_TX_BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content((new ObjectMapper()).writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(header().string(HttpHeaders.LOCATION,
                                                "/api/v1/accounts/" + accountNumber + "/transactions"))
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$").isNotEmpty())
                                .andExpect(jsonPath("$.txRef").isNotEmpty())
                                .andExpect(jsonPath("$.accountBalance", is(dto.getAccountBalance())))
                                .andExpect(jsonPath("$.amount", is(dto.getAmount())))
                                .andExpect(jsonPath("$.transactionAt", is(dto.getTransactionAt())))
                                .andExpect(jsonPath("$.description", is(dto.getDescription())))
                                .andExpect(jsonPath("$.operation", is(dto.getOperation())))
                                .andExpect(jsonPath("$.accountNumber", is(dto.getAccountNumber())));
        }

        @Test
        public void should_update_account_balance_when_do_withdrawal_insuffisant_amount() throws Exception {
                UUID accountNumber = UUID.randomUUID();
                TransactionRequest request = TransactionRequest.builder()
                                .accountNumber(accountNumber)
                                .amountInCents(500_00)
                                .operation(Transaction.TxType.WITHDRAWAL)
                                .description("Withdrawal amount>balance")
                                .build();

                Mockito.when(transactionService.doWithdrawalOn(Mockito.any(),
                                Mockito.anyInt(),
                                Mockito.anyString()))
                                .thenThrow(InvalidOperationException.class);

                mockMvc.perform(post(API_TX_BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content((new ObjectMapper()).writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        public void should_return_empty_list_when_no_transactions_history_found_for_an_existing_account()
                        throws Exception {
                UUID accountNumber = UUID.randomUUID();
                Mockito.when(transactionService.getTransactionsHistoryFor(Mockito.any()))
                                .thenReturn(List.of());

                mockMvc.perform(get(String.format(API_ACCOUNT_TX_BASE_URL, accountNumber))
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$").isEmpty());

        }

        @Test
        public void should_return_error_when_transactions_history_requested_for_no_existing_account() throws Exception {
                UUID accountNumber = UUID.randomUUID();
                Mockito.when(transactionService.getTransactionsHistoryFor(Mockito.any()))
                                .thenThrow(EntityNotFoundException.class);

                mockMvc.perform(get(String.format(API_ACCOUNT_TX_BASE_URL, accountNumber))
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());

        }

        @Test
        public void should_return_empty_list_when_transactions_history_found() throws Exception {
                UUID accountNumber = UUID.randomUUID();
                LocalDateTime now = LocalDateTime.now();
                Mockito.when(transactionService.getTransactionsHistoryFor(Mockito.any()))
                                .thenReturn(List.of(
                                                TransactionDto.builder()
                                                                .txRef(UUID.randomUUID().toString())
                                                                .accountNumber(accountNumber.toString())
                                                                .amount("10.00€")
                                                                .accountBalance("10.00€")
                                                                .description("Deposit of 10€")
                                                                .transactionAt(DatesFormatter
                                                                                .format(now.minusDays(100)))
                                                                .operation(Transaction.TxType.DEPOSIT.name())
                                                                .build(),
                                                TransactionDto.builder()
                                                                .txRef(UUID.randomUUID().toString())
                                                                .accountNumber(accountNumber.toString())
                                                                .amount("100.00€")
                                                                .accountBalance("110.00€")
                                                                .description("Deposit of 100€")
                                                                .transactionAt(DatesFormatter.format(now.minusDays(90)))
                                                                .operation(Transaction.TxType.DEPOSIT.name())
                                                                .build(),
                                                TransactionDto.builder()
                                                                .txRef(UUID.randomUUID().toString())
                                                                .accountNumber(accountNumber.toString())
                                                                .amount("50.00€")
                                                                .accountBalance("60.00€")
                                                                .description("Withdrawal of 50€")
                                                                .transactionAt(DatesFormatter.format(now.minusDays(80)))
                                                                .operation(Transaction.TxType.WITHDRAWAL.name())
                                                                .build(),
                                                TransactionDto.builder()
                                                                .txRef(UUID.randomUUID().toString())
                                                                .accountNumber(accountNumber.toString())
                                                                .amount("60.00€")
                                                                .accountBalance("0.00€")
                                                                .description("Withdrawal of 60€")
                                                                .transactionAt(DatesFormatter.format(now.minusDays(10)))
                                                                .operation(Transaction.TxType.WITHDRAWAL.name())
                                                                .build()));

                mockMvc.perform(get(String.format(API_ACCOUNT_TX_BASE_URL, accountNumber))
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$", hasSize(4)))
                                .andExpect(jsonPath("$[*].accountNumber", everyItem(is(accountNumber.toString()))))
                                .andExpect(jsonPath("$[*].txRef", everyItem(not(empty()))))
                                .andExpect(jsonPath("$[0].operation", is(Transaction.TxType.DEPOSIT.name())))
                                .andExpect(jsonPath("$[3].operation", is(Transaction.TxType.WITHDRAWAL.name())));

        }
}
