package com.harington.kata.bank.entity.dto;

import com.harington.kata.bank.entity.Account;
import com.harington.kata.bank.formatters.AmountFormatter;
import com.harington.kata.bank.formatters.DatesFormatter;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Value
public class AccountDto {
    String accountNumber;
    String currentBalance;
    String ownerName;
    String createdAt;

    public static AccountDto fromEntity(Account account) {
        if (account == null) return null;
        return AccountDto.builder()
                .currentBalance(AmountFormatter.formatCents(account.getCurrentBalanceInCents()))
                .ownerName(account.getOwnerName())
                .createdAt(DatesFormatter.format(account.getCreatedAt()))
                .accountNumber(account.getAccountNumber().toString())
                .build();
    }
}
