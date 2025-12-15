package com.bank.account.service;

import com.bank.account.dto.*;
import com.bank.account.exception.InsufficientFundsException;
import com.bank.account.kafka.KafkaTransactionProducer;
import com.bank.account.kafka.TransactionEvent;
import com.bank.account.model.Account;
import com.bank.account.model.Transaction;
import com.bank.account.repository.AccountRepository;
import com.bank.account.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final KafkaTransactionProducer kafkaProducer;

    @Transactional
    public AccountDTO createAccount(CreateAccountRequest request) {
        String accountNumber = generateAccountNumber();

        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setOwnerName(request.getOwnerName());
        account.setType(request.getType());
        account.setBalance(request.getInitialDeposit());

        account = accountRepository.save(account);

        createTransaction(account, request.getInitialDeposit(),
                Transaction.TransactionType.DEPOSIT, "Initial deposit");

        log.info("Account created: {}", accountNumber);
        return convertToDTO(account);
    }

    @Transactional(readOnly = true)
    public AccountDTO getAccount(String accountNumber) {
        Account account = findAccountByNumber(accountNumber);
        return convertToDTO(account);
    }

    @Transactional(readOnly = true)
    public List<TransactionDTO> getAccountTransaction(String accountNumber) {
        Account account = findAccountByNumber(accountNumber);
        return transactionRepository.findByAccountOrderByTimestamp(account)
                .stream()
                .map(this::convertToTransactionDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TransactionDTO deposit(String accountNumber, TransactionRequest request) {
        Account account = findAccountByNumber(accountNumber);

        BigDecimal newBalance = account.getBalance().add(request.getAmount());
        account.setBalance(newBalance);
        accountRepository.save(account);

        Transaction transaction = createTransaction(account, request.getAmount(),
                Transaction.TransactionType.DEPOSIT,
                request.getDescription());

        sendTransactionEvent(transaction);
        return convertToTransactionDTO(transaction);
    }

    @Transactional
    public TransactionDTO withdraw(String accountNumber, TransactionRequest request) {
        Account account = findAccountByNumber(accountNumber);

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds for withdrawal");
        }

        BigDecimal newBalance = account.getBalance().subtract(request.getAmount());
        account.setBalance(newBalance);
        accountRepository.save(account);

        Transaction transaction = createTransaction(account, request.getAmount(),
                Transaction.TransactionType.WITHDRAWAL,
                request.getDescription());

        sendTransactionEvent(transaction);
        return convertToTransactionDTO(transaction);
    }

    @Transactional
    public TransferResultDTO transfer(TransferRequest request) {
        Account fromAccount = findAccountByNumber(request.getFromAccountNumber());
        Account toAccount = findAccountByNumber(request.getToAccountNumber());

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds for transfer");
        }

        // Снимаем со счёта отправителя
        BigDecimal fromNewBalance = fromAccount.getBalance().subtract(request.getAmount());
        fromAccount.setBalance(fromNewBalance);
        accountRepository.save(fromAccount);

        Transaction fromTransaction = createTransaction(fromAccount, request.getAmount(),
                Transaction.TransactionType.TRANSFER_OUT,
                "Transfer to " + toAccount.getAccountNumber()
        );

        // Добавляем на счёт получателя
        BigDecimal toNewBalance = toAccount.getBalance().add(request.getAmount());
        toAccount.setBalance(toNewBalance);
        accountRepository.save(toAccount);

        Transaction toTransaction = createTransaction(toAccount, request.getAmount(),
                Transaction.TransactionType.TRANSFER_IN,
                "Transfer from " + fromAccount.getAccountNumber()
        );

        // Добавляем события в Kafka
        sendTransactionEvent(fromTransaction);
        sendTransactionEvent(toTransaction);

        log.info("Transfer completed: {} -> {}, amount: {}",
                fromAccount.getAccountNumber(),
                toAccount.getAccountNumber(),
                request.getAmount()
        );

        return  convertToTransferResultDTO(
                convertToTransactionDTO(fromTransaction),
                convertToTransactionDTO(toTransaction),
                request.getAmount()
        );
    }

    private void sendTransactionEvent(Transaction transaction) {
        TransactionEvent event = new TransactionEvent(
                transaction.getId(),
                transaction.getAccount().getAccountNumber(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getDescription(),
                transaction.getBalanceAfter(),
                transaction.getTimestamp()
        );

        kafkaProducer.sendTransactionEvent(event);
    }

    private Account findAccountByNumber(String accountNumber) {
        try {
            return accountRepository.findByAccountNumber(accountNumber)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
        } catch (AccountNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Transaction createTransaction(Account account, BigDecimal amount,
                                          Transaction.TransactionType type, String description) {
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setDescription(description);
        transaction.setBalanceAfter(account.getBalance());

        return transactionRepository.save(transaction);
    }

    private String generateAccountNumber() {
        return "ACC" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }

    private AccountDTO convertToDTO(Account account) {
        AccountDTO dto = new AccountDTO();
        dto.setId(account.getId());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setOwnerName(account.getOwnerName());
        dto.setBalance(account.getBalance());
        dto.setType(account.getType());
        return dto;
    }

    private TransactionDTO convertToTransactionDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setAccountNumber(transaction.getAccount().getAccountNumber());
        dto.setAmount(transaction.getAmount());
        dto.setType(transaction.getType());
        dto.setDescription(transaction.getDescription());
        dto.setTimestamp(transaction.getTimestamp());
        dto.setBalanceAfter(transaction.getBalanceAfter());
        return dto;
    }

    private TransferResultDTO convertToTransferResultDTO(
            TransactionDTO fromTransaction,
            TransactionDTO toTransaction,
            BigDecimal amount) {

        TransferResultDTO dto = new TransferResultDTO();

        dto.setFromTransaction(fromTransaction);
        dto.setToTransaction(toTransaction);
        dto.setTransferAmount(amount);
        return dto;
    }
}
