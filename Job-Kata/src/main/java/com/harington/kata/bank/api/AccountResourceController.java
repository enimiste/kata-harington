package com.harington.kata.bank.api;

import com.harington.kata.bank.entity.Transaction.TxType;
import com.harington.kata.bank.entity.dto.AccountDto;
import com.harington.kata.bank.entity.dto.AccountRequest;
import com.harington.kata.bank.entity.dto.TransactionDto;
import com.harington.kata.bank.entity.dto.TransactionRequest;
import com.harington.kata.bank.service.AccountService;
import com.harington.kata.bank.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/accounts", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
        MediaType.APPLICATION_JSON_VALUE })
@RequiredArgsConstructor
public class AccountResourceController {
    private final AccountService accountService;
    private final TransactionService transactionService;

    @GetMapping("")
    public ResponseEntity<List<AccountDto>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @PostMapping("")
    public ResponseEntity<AccountDto> createNewAccount(@Valid @RequestBody AccountRequest request) {
        AccountDto dto = accountService.createNewAccount(request.getOwnerName(), request.getInitialBalanceInCents());
        return ResponseEntity.created(URI.create("/api/v1/accounts/" + dto.getAccountNumber())).body(dto);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountDto> getAccountByNumber(@PathVariable("accountNumber") UUID accountNumber) {
        return accountService.findByAccountNumber(accountNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{accountNumber}/transactions")
    public ResponseEntity<List<TransactionDto>> getAllTransactionsByAccount(
            @PathVariable("accountNumber") UUID accountNumber) {
        return ResponseEntity.ok(transactionService.getTransactionsHistoryFor(accountNumber));
    }

    @PostMapping("/transactions")
    public ResponseEntity<TransactionDto> doOperation(@Valid @RequestBody TransactionRequest request) {
        TransactionDto tx;
        if (request.getOperation() == TxType.DEPOSIT)
            tx = transactionService.doDepositOn(request.getAccountNumber(), request.getAmountInCents(),
                    request.getDescription());
        else
            tx = transactionService.doWithdrawalOn(request.getAccountNumber(),
                    request.getAmountInCents(),
                    request.getDescription());
        return ResponseEntity
                .created(URI.create("/api/v1/accounts/" + request.getAccountNumber() + "/transactions"))
                .body(tx);
    }
}
