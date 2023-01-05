package com.harington.kata.bank.entity.dto.transformers;

import com.harington.kata.bank.entity.Account;
import com.harington.kata.bank.entity.dto.AccountDto;
import com.harington.kata.bank.formatters.AmountFormatter;
import com.harington.kata.bank.formatters.DatesFormatter;

public class AccountDtoTransformer {


    public static AccountDto fromEntity(Account account) {
        if (account == null)
            return null;
        return AccountDto.builder()
                .currentBalance(AmountFormatter.formatCents(account.getCurrentBalanceInCents()))
                .ownerName(account.getOwnerName())
                .createdAt(DatesFormatter.format(account.getCreatedAt()))
                .accountNumber(account.getAccountNumber().toString())
                .build();
    }
}
