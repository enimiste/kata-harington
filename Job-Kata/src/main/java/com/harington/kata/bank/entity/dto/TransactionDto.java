package com.harington.kata.bank.entity.dto;

import com.harington.kata.bank.entity.Account;
import com.harington.kata.bank.entity.Transaction;
import com.harington.kata.bank.formatters.AmountFormatter;
import com.harington.kata.bank.formatters.DatesFormatter;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Value
@Builder
public class TransactionDto {
    String txRef;
    String accountBalance;
    String amount;
    String transactionAt;
    String description;
    String accountNumber;
    String operation;

    public static TransactionDto fromEntity(Transaction tx) {
        if (tx == null) return null;
        TransactionDtoBuilder builder = TransactionDto.builder()
                .txRef(tx.getTxRef().toString())
                .transactionAt(DatesFormatter.format(tx.getTransactionAt()))
                .amount(AmountFormatter.formatCents(tx.getAmountInCents()))
                .accountBalance(AmountFormatter.formatCents(tx.getPostTxAccountBalanceInCents()))
                .operation(tx.getTxType().name())
                .description(tx.getDescription());
        if (tx.getAccount() != null)
            builder = builder.accountNumber(tx.getAccount().getAccountNumber().toString());
        return builder.build();
    }
}
