package com.bank.account.unit.service;

import com.bank.account.dto.CreateAccountRequest;
import com.bank.account.dto.TransactionRequest;
import com.bank.account.model.Account;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;

class AccountServiceTest {

    private Account testAccount;
    private CreateAccountRequest createRequest;
    private TransactionRequest transactionRequest;

    @BeforeEach
    void setUp() {
        // Подготовка тестовых данных перед каждым тестом
        testAccount = new Account();
        testAccount.setId(123L);
        testAccount.setAccountNumber("ACC123");
        testAccount.setOwnerName("John Doe");
        testAccount.setBalance(new BigDecimal("1000.00"));
        testAccount.setType(Account.AccountType.CHECKING);

        createRequest = new CreateAccountRequest();
        createRequest.setOwnerName("John Doe");
        createRequest.setType(Account.AccountType.SAVINGS);
        createRequest.setInitialDeposit(new BigDecimal("500.00"));

        transactionRequest = new TransactionRequest();
        transactionRequest.setAmount(new BigDecimal("100.00"));
        transactionRequest.setDescription("Test transaction");
    }
}