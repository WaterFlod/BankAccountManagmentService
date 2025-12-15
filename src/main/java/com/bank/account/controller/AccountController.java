package com.bank.account.controller;

import com.bank.account.dto.*;
import com.bank.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    @Autowired
    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        AccountDTO account = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountDTO> getAccount(@PathVariable String accountNumber) {
        AccountDTO account = accountService.getAccount(accountNumber);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/{accountNumber}/transactions")
    public ResponseEntity<List<TransactionDTO>> getTransactions(@PathVariable String accountNumber) {
        List<TransactionDTO> transactions = accountService.getAccountTransaction(accountNumber);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<TransactionDTO> deposit(
            @PathVariable String accountNumber,
            @Valid @RequestBody TransactionRequest request) {
        TransactionDTO transaction = accountService.deposit(accountNumber, request);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/{accountNumber}/withdraw")
    public ResponseEntity<TransactionDTO> withdraw(
            @PathVariable String accountNumber,
            @Valid @RequestBody TransactionRequest request) {
        TransactionDTO transaction = accountService.withdraw(accountNumber, request);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransferResultDTO> transfer(@Valid @RequestBody TransferRequest request) {
        TransferResultDTO result = accountService.transfer(request);
        return ResponseEntity.ok(result);
    }
}
