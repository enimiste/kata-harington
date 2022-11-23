package com.harington.kata.bank.entity.dto;

import com.harington.kata.bank.entity.Transaction;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import javax.validation.constraints.Min;
import java.util.UUID;

@Builder
@Value
public class TransactionRequest {
    @NonNull
    UUID accountNumber;
    @Min(1)
    int amountInCents;
    String description;
    @NonNull
    Transaction.TxType operation;
}
