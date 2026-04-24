package com.bank.account.service;

import com.bank.account.dto.TransactionResponse;
import com.bank.account.dto.TransactionRequest;
import com.bank.account.dto.TransferResponse;
import com.bank.account.model.Account;
import com.bank.account.model.Transaction;
import com.bank.account.model.TransactionType;
import com.bank.account.repository.AccountRepository;
import com.bank.account.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final AccountRepository accountRepository;

    public List<TransactionResponse> getAccountTransaction(String acc) {
        return transactionRepository.findByAccountNumber(acc)
                .stream()
                .map(this::convertToTransactionResponse)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getLastTransaction(String userId) {
        Pageable topTen = PageRequest.of(0, 10, Sort.unsorted());

        List<Transaction> transactions = transactionRepository.findTop10ByUserId(userId, topTen);

        return transactions.stream()
                .map(this::convertToTransactionResponse)
                .toList();
    }

    @Transactional
    public TransactionResponse deposit(Account account, BigDecimal amount, String desc) {
        Transaction transaction = Transaction.builder()
                .account(account)
                .amount(amount)
                .type(TransactionType.DEPOSIT)
                .description(desc)
                .balanceAfter(account.getBalance())
                .build();

        transactionRepository.save(transaction);

        return convertToTransactionResponse(transaction);
    }

    @Transactional
    public TransactionResponse withdraw(Account account, BigDecimal amount, String desc) {
        Transaction transaction = Transaction.builder()
                .account(account)
                .amount(amount)
                .type(TransactionType.WITHDRAWAL)
                .description(desc)
                .balanceAfter(account.getBalance())
                .build();

        transactionRepository.save(transaction);

        return convertToTransactionResponse(transaction);
    }

    @Transactional
    public TransferResponse transferFrom(Account fromAccount, String toAccountNumber, BigDecimal amount) {
        Transaction transfer = Transaction.builder()
                .account(fromAccount)
                .amount(amount)
                .type(TransactionType.TRANSFER_OUT)
                .description("Перевод на счет: " + toAccountNumber)
                .build();

        transactionRepository.save(transfer);

        return new TransferResponse(
                transfer.getAccount().getAccountNumber(),
                toAccountNumber,
                transfer.getAmount(),
                transfer.getBalanceAfter(),
                transfer.getTimestamp()
                );
    }

    @Transactional
    public void transferTo(Account toAccount, String fromAccountNumber, BigDecimal amount) {
        Transaction transfer = Transaction.builder()
                .account(toAccount)
                .amount(amount)
                .type(TransactionType.TRANSFER_IN)
                .description("Перевод с счета: " + fromAccountNumber)
                .build();

        transactionRepository.save(transfer);
    }

    private TransactionResponse convertToTransactionResponse(Transaction transaction) {
        TransactionResponse dto = new TransactionResponse(
                transaction.getAccount().getAccountNumber(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getType(),
                (DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")).format(transaction.getTimestamp()),
                transaction.getBalanceAfter()
        );

        return dto;
    }
}
