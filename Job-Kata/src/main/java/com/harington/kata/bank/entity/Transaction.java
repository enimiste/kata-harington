package com.harington.kata.bank.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    @NotNull
    protected UUID txRef;

    @Min(0)
    protected int postTxAccountBalanceInCents;
    @Min(1)
    protected int amountInCents;
    @PastOrPresent
    protected LocalDateTime transactionAt;
    protected String description;
    @ManyToOne
    protected Account account;
    @NotNull
    @Column(length = 20)
    protected TxType txType;

    public enum TxType {
        DEPOSIT, WITHDRAWAL
    }
}
