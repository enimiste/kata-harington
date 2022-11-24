package com.harington.kata.bank.service;

import com.harington.kata.bank.entity.Account;
import com.harington.kata.bank.entity.Transaction;
import com.harington.kata.bank.entity.dto.TransactionDto;
import com.harington.kata.bank.exceptions.EntityNotFoundException;
import com.harington.kata.bank.exceptions.InvalidOperationException;
import com.harington.kata.bank.formatters.AmountFormatter;
import com.harington.kata.bank.repository.AccountRepository;
import com.harington.kata.bank.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransactionServiceTest {

        @MockBean
        AccountRepository accountRepository;
        @MockBean
        TransactionRepository transactionRepository;

        @Autowired
        TransactionService transactionService;

        @Test
        void should_return_error_when_do_operation_on_no_existing_account() {
                Mockito.when(accountRepository.findOneByAccountNumber(Mockito.any()))
                                .thenReturn(Optional.empty());

                assertThrows(EntityNotFoundException.class, () -> {
                        transactionService.doDepositOn(UUID.randomUUID(), 10000, "Deposit of 100€");
                });
        }

        @Test
        void should_return_error_when_do_operation_with_invalid_request_inputs() {
                assertThrows(ConstraintViolationException.class, () -> {
                        transactionService.doDepositOn(UUID.randomUUID(), 0, "Deposit of 0€");
                });
                assertThrows(ConstraintViolationException.class, () -> {
                        transactionService.doDepositOn(UUID.randomUUID(), -1, "Deposit of -1€");
                });
                assertThrows(ConstraintViolationException.class, () -> {
                        transactionService.doDepositOn(null, 10, "Deposit of 10€");
                });
        }

        @Test
        void should_return_error_when_do_withdrawal_with_account_balance_insuffisante() {
                assertThrows(InvalidOperationException.class, () -> {
                        UUID accountNumber = UUID.randomUUID();
                        Account account = Account.builder()
                                        .id(2L)
                                        .accountNumber(accountNumber)
                                        .initialBalanceInCents(100_00)
                                        .currentBalanceInCents(100_00)
                                        .ownerName("Anis BESSA")
                                        .createdAt(LocalDateTime.now())
                                        .build();
                        Mockito.when(accountRepository.findOneByAccountNumber(Mockito.any()))
                                        .thenReturn(Optional.of(account));
                        transactionService.doWithdrawalOn(accountNumber, 1000_00, "Deposit of 1000€");
                });
        }

        @Test
        void should_return_tx_when_do_deposit_on_existing_account() {
                int accountBalance = 200_00;// 200€
                UUID accountNumber = UUID.randomUUID();
                Account account = Account.builder()
                                .id(2L)
                                .accountNumber(accountNumber)
                                .initialBalanceInCents(accountBalance)
                                .currentBalanceInCents(accountBalance)
                                .ownerName("Anis BESSA")
                                .createdAt(LocalDateTime.now())
                                .build();
                Mockito.when(accountRepository.findOneByAccountNumber(Mockito.any()))
                                .thenReturn(Optional.of(account));
                Mockito.when(accountRepository.save(account))
                                .thenReturn(account);

                TransactionDto tx = transactionService.doDepositOn(accountNumber, 1000, "Deposit of 00€");

                assertNotNull(tx);
                assertEquals("10.00€", tx.getAmount());
                assertNotNull(tx.getTxRef());
                assertEquals(accountNumber.toString(), tx.getAccountNumber());
                assertEquals(AmountFormatter.formatCents(accountBalance + 1000), tx.getAccountBalance());
                assertNotNull(tx.getTransactionAt());
                assertEquals(Transaction.TxType.DEPOSIT.name(), tx.getOperation());
        }

        @Test
        void should_return_tx_when_do_withdrawal_on_existing_account() {
                int accountBalance = 2000_00;// 200€
                UUID accountNumber = UUID.randomUUID();
                Account account = Account.builder()
                                .id(2L)
                                .accountNumber(accountNumber)
                                .initialBalanceInCents(accountBalance)
                                .currentBalanceInCents(accountBalance)
                                .ownerName("Anis BESSA")
                                .createdAt(LocalDateTime.now())
                                .build();
                Mockito.when(accountRepository.findOneByAccountNumber(Mockito.any()))
                                .thenReturn(Optional.of(account));
                Mockito.when(accountRepository.save(account))
                                .thenReturn(account);

                TransactionDto tx = transactionService.doWithdrawalOn(accountNumber, 1000, "Deposit of 00€");

                assertNotNull(tx);
                assertEquals("10.00€", tx.getAmount());
                assertNotNull(tx.getTxRef());
                assertEquals(accountNumber.toString(), tx.getAccountNumber());
                assertEquals(AmountFormatter.formatCents(accountBalance - 1000), tx.getAccountBalance());
                assertNotNull(tx.getTransactionAt());
                assertEquals(Transaction.TxType.WITHDRAWAL.name(), tx.getOperation());
        }

        @Test
        void should_return_error_when_requesting_history_for_no_existing_account() {
                assertThrows(EntityNotFoundException.class, () -> {
                        List<TransactionDto> txs = transactionService.getTransactionsHistoryFor(UUID.randomUUID());
                });
        }

        @Test
        void should_return_empty_list_when_requesting_history_for_existing_account_without_any_tx() {
                Account account = Account.builder()
                                .id(2L)
                                .accountNumber(UUID.randomUUID())
                                .initialBalanceInCents(0)
                                .currentBalanceInCents(0)
                                .ownerName("Anis BESSA")
                                .createdAt(LocalDateTime.now())
                                .build();
                Mockito.when(accountRepository.findOneByAccountNumber(Mockito.any()))
                                .thenReturn(Optional.of(account));
                List<TransactionDto> txs = transactionService.getTransactionsHistoryFor(UUID.randomUUID());
                assertNotNull(txs);
                assertEquals(0, txs.size());
        }

        @Test
        void should_return_list_when_requesting_history_for_existing_account_with_txs() {
                UUID accountNumber = UUID.randomUUID();
                Account account = Account.builder()
                                .id(2L)
                                .accountNumber(accountNumber)
                                .initialBalanceInCents(100_00)
                                .currentBalanceInCents(200_00)
                                .ownerName("Anis BESSA")
                                .createdAt(LocalDateTime.now().minusMonths(10))
                                .build();
                List.of(
                                Transaction.builder()
                                                .txRef(UUID.randomUUID())
                                                .transactionAt(LocalDateTime.now().minusDays(30))
                                                .description("Deposit 10€")
                                                .txType(Transaction.TxType.DEPOSIT)
                                                .amountInCents(10_00)
                                                .postTxAccountBalanceInCents(110_00)
                                                .build(),
                                Transaction.builder()
                                                .txRef(UUID.randomUUID())
                                                .transactionAt(LocalDateTime.now().minusDays(25))
                                                .description("Deposit 50€")
                                                .txType(Transaction.TxType.DEPOSIT)
                                                .amountInCents(50_00)
                                                .postTxAccountBalanceInCents(160_00)
                                                .build(),
                                Transaction.builder()
                                                .txRef(UUID.randomUUID())
                                                .transactionAt(LocalDateTime.now().minusDays(20))
                                                .description("Deposit 100€")
                                                .txType(Transaction.TxType.DEPOSIT)
                                                .amountInCents(100_00)
                                                .postTxAccountBalanceInCents(260_00)
                                                .build(),
                                Transaction.builder()
                                                .txRef(UUID.randomUUID())
                                                .transactionAt(LocalDateTime.now().minusDays(30))
                                                .description("Withdrawal 60€")
                                                .txType(Transaction.TxType.WITHDRAWAL)
                                                .amountInCents(60_00)
                                                .postTxAccountBalanceInCents(200_00)
                                                .build())
                                .forEach(account::addTx);
                Mockito.when(accountRepository.findOneByAccountNumber(Mockito.any()))
                                .thenReturn(Optional.of(account));
                List<TransactionDto> txs = transactionService.getTransactionsHistoryFor(accountNumber);
                assertNotNull(txs);
                assertEquals(4, txs.size());
                assertTrue(txs.stream().allMatch(x -> Objects.equals(x.getAccountNumber(), accountNumber.toString())));
        }
}