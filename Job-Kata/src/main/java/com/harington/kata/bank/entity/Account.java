package com.harington.kata.bank.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "ACCOUNTS_ACCOUNT_NUMBER_UK", columnNames = {Account.ACCOUNT_NUMBER_COL})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Account {
    public static final String ACCOUNT_NUMBER_COL = "ACCOUNT_NUMBER";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = ACCOUNT_NUMBER_COL)
    private UUID accountNumber;
    @Min(0)
    private int initialBalanceInCents;
    @Min(0)
    private int currentBalanceInCents;
    @NotNull
    @NotEmpty
    @Size(min = 3)
    private String ownerName;
    @PastOrPresent
    private LocalDateTime createdAt;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Builder.Default
    @ToString.Exclude
    private Set<Transaction> transactions = new HashSet<>();

    public void addTx(Transaction tx) {
        if (tx == null) return;
        tx.setAccount(this);
        transactions.add(tx);
    }

    public void removeTx(Transaction tx) {
        if (tx == null) return;
        tx.setAccount(null);
        transactions.remove(tx);
    }

    public void incrementBalanceBy(int amountInCents) {
        this.currentBalanceInCents += amountInCents;
    }
}
