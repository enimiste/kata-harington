package com.harington.kata.bank.service;

import com.harington.kata.bank.entity.Account;
import com.harington.kata.bank.entity.dto.AccountDto;
import com.harington.kata.bank.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class AccountService {
    private final AccountRepository accountRepository;

    public @NonNull AccountDto createNewAccount(
            @NotNull @NotEmpty @Size(min = 3) String ownerName,
            @Min(0) int initialBalanceInCents
    ) {
        Account account = accountRepository.save(Account.builder()
                .createdAt(LocalDateTime.now())
                .ownerName(ownerName)
                .currentBalanceInCents(initialBalanceInCents)
                .initialBalanceInCents(initialBalanceInCents)
                .accountNumber(UUID.randomUUID())
                .build());
        return AccountDto.fromEntity(account);
    }

    public List<AccountDto> getAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(AccountDto::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<AccountDto> findByAccountNumber(UUID accountNumber) {
        return accountRepository.findOneByAccountNumber(accountNumber)
                .map(AccountDto::fromEntity);
    }
}
