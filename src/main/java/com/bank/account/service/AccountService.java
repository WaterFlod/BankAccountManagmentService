package com.bank.account.service;

import com.bank.account.dto.*;
import com.bank.account.exception.AccountNotFoundException;
import com.bank.account.exception.InsufficientFundsException;
import com.bank.account.model.Account;
import com.bank.account.model.User;
import com.bank.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionService transactionService;
    private final UserService userService;

    @Transactional
    public TransactionResponse createAccount(CreateAccountRequest request) {
        String accountNumber = generateAccountNumber();

        User user = userService.findUserByIdentifier(request.getIdentifier());

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .type(request.getType())
                .user(user)
                .balance(request.getInitialDeposit())
                .build();

        account = accountRepository.save(account);

        TransactionResponse response = transactionService.deposit(
                account,
                request.getInitialDeposit(),
                "Первоначальный депозит"
        );

        log.info("Счет создан: {}", accountNumber);
        return response;
    }

    @Transactional(readOnly = true)
    public AccountDTO getAccount(String accountNumber) {
        Account account = findAccountByNumber(accountNumber);
        return convertToDTO(account);
    }

    @Transactional
    public TransactionResponse deposit(String accountNumber, TransactionRequest request) {
        Account account = findAccountByNumber(accountNumber);

        BigDecimal newBalance = account.getBalance().add(request.getAmount());
        account.setBalance(newBalance);
        accountRepository.save(account);

        TransactionResponse response = transactionService.deposit(
                account,
                request.getAmount(),
                "Пополнение счета " + account.getAccountNumber() + " на сумму " + request.getAmount()
        );

        return response;
    }

    @Transactional
    public TransactionResponse withdraw(String accountNumber, TransactionRequest request) {
        Account account = findAccountByNumber(accountNumber);

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException(accountNumber, account.getBalance(),request.getAmount());
        }

        BigDecimal newBalance = account.getBalance().subtract(request.getAmount());
        account.setBalance(newBalance);

        accountRepository.save(account);

        TransactionResponse response = transactionService.withdraw(
                account,
                request.getAmount(),
                "Убыль счета " + account.getAccountNumber() + " на сумму " + request.getAmount()
        );

        return response;
    }

    @Transactional
    public TransferResponse transfer(TransferRequest request) {
        Account fromAccount = findAccountByNumber(request.getFromAccountNumber());
        Account toAccount = findAccountByNumber(request.getToAccountNumber());

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException(fromAccount.getAccountNumber(), fromAccount.getBalance(), request.getAmount());
        }

        // Снимаем со счёта отправителя
        BigDecimal fromNewBalance = fromAccount.getBalance().subtract(request.getAmount());
        fromAccount.setBalance(fromNewBalance);
        accountRepository.save(fromAccount);

        TransferResponse response = transactionService.transferFrom(
                fromAccount,
                toAccount.getAccountNumber(),
                request.getAmount()
        );

        // Добавляем на счёт получателя
        BigDecimal toNewBalance = toAccount.getBalance().add(request.getAmount());
        toAccount.setBalance(toNewBalance);
        accountRepository.save(toAccount);

        transactionService.transferTo(toAccount, fromAccount.getAccountNumber(), request.getAmount());

        log.info("Перевод выполнен: {} -> {}, сумма: {}",
                fromAccount.getAccountNumber(),
                toAccount.getAccountNumber(),
                request.getAmount()
        );

        return response;
    }

    private Account findAccountByNumber(String accountNumber) {
            return accountRepository.findByAccountNumber(accountNumber)
                    .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    private String generateAccountNumber() {
        return "ACC" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }

    private AccountDTO convertToDTO(Account account) {
        AccountDTO dto = new AccountDTO();
        dto.setId(account.getId());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setBalance(account.getBalance());
        dto.setType(account.getType());
        return dto;
    }
}
