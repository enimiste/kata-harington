package com.harington.kata.bank.service;

import com.harington.kata.bank.entity.Account;
import com.harington.kata.bank.entity.dto.AccountDto;
import com.harington.kata.bank.exceptions.InvalidOperationException;
import com.harington.kata.bank.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AccountServiceTest {

    @MockBean
    AccountRepository accountRepository;
    @Autowired
    AccountService accountService;

    @Test
    void should_return_created_account() {
        String owner = "Anis BESSA";
        int initialBalance = 1;

        UUID accountNumber = UUID.randomUUID();
        Account account = Account.builder()
                .id(1L)
                .accountNumber(accountNumber)
                .initialBalanceInCents(1)
                .currentBalanceInCents(1)
                .ownerName(owner)
                .createdAt(LocalDateTime.now())
                .build();
        Mockito.when(accountRepository.save(Mockito.any()))
                .thenReturn(account);
        AccountDto dto = accountService.createNewAccount(owner, initialBalance);
        assertNotNull(dto);
        assertEquals("0.01€", dto.getCurrentBalance());
        assertEquals(owner, dto.getOwnerName());
        assertNotNull(dto.getAccountNumber());
    }

    @Test
    void should_return_error_when_initial_balance_negative() {
        assertThrows(ConstraintViolationException.class, () -> {
            accountService.createNewAccount("Anis BESSA", -1);
        });
    }

    @Test
    void should_return_error_when_ownerName_null() {
        assertThrows(ConstraintViolationException.class, () -> {
            accountService.createNewAccount(null, 10);
        });
    }

    @Test
    void should_return_error_when_ownerName_has_less_chars() {
        assertThrows(ConstraintViolationException.class, () -> {
            accountService.createNewAccount("ab", 10);
        });
    }

    @Test
    void should_return_empty_list_when_no_account_exist() {
        List<AccountDto> accounts = accountService.getAllAccounts();
        assertNotNull(accounts);
        assertTrue(accounts.isEmpty());
    }

    @Test
    void should_return_accounts_list() {
        UUID accountNumber1 = UUID.randomUUID();
        UUID accountNumber2 = UUID.randomUUID();
        Mockito.when(accountRepository.findAll())
                .thenReturn(List.of(
                        Account.builder()
                                .id(1L)
                                .accountNumber(accountNumber1)
                                .initialBalanceInCents(10)
                                .currentBalanceInCents(10)
                                .ownerName("NOUNI EL Bachir")
                                .createdAt(LocalDateTime.now())
                                .build(),
                        Account.builder()
                                .id(2L)
                                .accountNumber(accountNumber2)
                                .initialBalanceInCents(100)
                                .currentBalanceInCents(100)
                                .ownerName("Anis BESSA")
                                .createdAt(LocalDateTime.now())
                                .build()
                ));
        List<AccountDto> accounts = accountService.getAllAccounts();
        assertNotNull(accounts);
        assertEquals(2, accounts.size());
        assertEquals(accountNumber1.toString(), accounts.get(0).getAccountNumber());
    }

    @Test
    void should_return_empty_optional_when_no_account_exists() {
        UUID accountNumber = UUID.randomUUID();
        Mockito.when(accountRepository.findOneByAccountNumber(Mockito.any()))
                .thenReturn(Optional.empty());
        Optional<AccountDto> dto = accountService.findByAccountNumber(accountNumber);
        assertNotNull(dto);
        assertTrue(dto.isEmpty());
    }

    @Test
    void should_return_account_dto_when_found() {
        UUID accountNumber = UUID.randomUUID();
        Mockito.when(accountRepository.findOneByAccountNumber(Mockito.any()))
                .thenReturn(Optional.of(
                        Account.builder()
                                .id(2L)
                                .accountNumber(accountNumber)
                                .initialBalanceInCents(1000)
                                .currentBalanceInCents(1000)
                                .ownerName("Anis BESSA")
                                .createdAt(LocalDateTime.now())
                                .build()
                ));
        Optional<AccountDto> dto = accountService.findByAccountNumber(accountNumber);
        assertNotNull(dto);
        assertTrue(dto.isPresent());
        assertEquals(accountNumber.toString(), dto.get().getAccountNumber());
    }
}