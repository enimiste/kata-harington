package com.harington.kata.bank.repository;

import com.harington.kata.bank.entity.Account;
import com.harington.kata.bank.entity.Transaction;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AccountRepositoryTest {

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
    void should_return_empty_when_finding_account_by_null_number() {
        assertTrue(accountRepository.findOneByAccountNumber(null).isEmpty());
    }

    @Test
    void should_return_empty_when_finding_account_by_no_existing_number() {
        assertTrue(accountRepository.findOneByAccountNumber(UUID.randomUUID()).isEmpty());
    }

    @Test
    void should_return_account_when_finding_account_by_existing_number() {
        Account acc = accountRepository.findOneByAccountNumber(accountNumber1).orElse(null);
        assertNotNull(acc);
        assertEquals(accountNumber1, acc.getAccountNumber());
    }
}