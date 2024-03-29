package com.harington.kata.bank.service;

import com.harington.kata.bank.entity.Transaction;
import com.harington.kata.bank.entity.dto.TransactionDto;
import com.harington.kata.bank.exceptions.EntityNotFoundException;
import com.harington.kata.bank.exceptions.InvalidOperationException;
import com.harington.kata.bank.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class TransactionService {
    private final AccountRepository accountRepository;

    @Transactional
    public TransactionDto doDepositOn(@NotNull UUID accountNumber,
                                      @Min(1) int amountInCents,
                                      @NotNull String description,
                                      int accountVersion) {
        return accountRepository.findOneByAccountNumber(accountNumber)//A
                .map(acc -> {
                    if (acc.getVersion() != accountVersion)
                        throw new InvalidOperationException(String.format("Account with ID %s is no longer valid. Please refresh the page", acc.getId()));
                    Transaction tx = Transaction.builder()
                            .txRef(UUID.randomUUID())
                            .transactionAt(LocalDateTime.now())
                            .description(description)
                            .txType(Transaction.TxType.DEPOSIT)
                            .amountInCents(amountInCents)
                            .postTxAccountBalanceInCents(acc.getCurrentBalanceInCents() + amountInCents)
                            .build();
                    acc.incrementBalanceBy(amountInCents);
                    acc.addTx(tx);
                    accountRepository.save(acc);//B
//                    (new MailingService()).sendNotification(new MsgDto("Msg : " + acc.getId()));
                    return TransactionDto.fromEntity(tx);
                }).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public TransactionDto doWithdrawalOn(@NotNull UUID accountNumber,
                                         @Min(1) int amountInCents,
                                         @NotNull String description,
                                         int accountVersion) {
        return accountRepository.findOneByAccountNumber(accountNumber)
                .map(acc -> {
                    if (acc.getVersion() != accountVersion)
                        throw new InvalidOperationException(String.format("Account with ID %s is no longer valid. Please refresh the page", acc.getId()));
                    if (acc.getCurrentBalanceInCents() < amountInCents)
                        throw new InvalidOperationException(String.format("Account's (ID %s) balance is not enough", acc.getId()));

                    Transaction tx = Transaction.builder()
                            .txRef(UUID.randomUUID())
                            .transactionAt(LocalDateTime.now())
                            .description(description)
                            .txType(Transaction.TxType.WITHDRAWAL)
                            .amountInCents(amountInCents)
                            .postTxAccountBalanceInCents(acc.getCurrentBalanceInCents() - amountInCents)
                            .build();
                    acc.incrementBalanceBy(-amountInCents);
                    acc.addTx(tx);
                    accountRepository.save(acc);
                    return TransactionDto.fromEntity(tx);
                }).orElseThrow(EntityNotFoundException::new);
    }

    /**
     * Returns an account's list of transactions ordered by creation datetime in the
     * decreasing order
     *
     * @param accountNumber
     * @return
     */
    public List<TransactionDto> getTransactionsHistoryFor(@NotNull UUID accountNumber) {
        return accountRepository.findOneByAccountNumber(accountNumber)
                .map(xs -> xs.getTransactions()
                        .stream()
                        .map(TransactionDto::fromEntity)
                        .collect(Collectors.toList()))
                .orElseThrow(EntityNotFoundException::new);
    }
}
