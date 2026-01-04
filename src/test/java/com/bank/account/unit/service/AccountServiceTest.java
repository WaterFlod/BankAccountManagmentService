package com.bank.account.unit.service;

import com.bank.account.dto.AccountDTO;
import com.bank.account.dto.CreateAccountRequest;
import com.bank.account.dto.TransactionRequest;
import com.bank.account.model.Account;
import com.bank.account.repository.AccountRepository;
import com.bank.account.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private Account testAccount;

    @Mock
    private CreateAccountRequest createRequest;

    @Mock
    private TransactionRequest transactionRequest;

    @Mock
    AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

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

    @DisplayName("Создание счёта с валидным запросом должно возвращать валидный AccountDTO")
    @Test
    void createAccountWithValidRequest_returnsValidAccountDTO() {
        AccountDTO result = accountService.createAccount(createRequest);

        assertNotNull(result);
        assertTrue(result.getAccountNumber().startsWith("ACC"));
        assertEquals(result.getOwnerName(), "John Doe");
        assertEquals(result.getBalance(), new BigDecimal("500"));
        assertEquals(result.getType(), Account.AccountType.SAVINGS);

        verify(accountRepository).save(any(Account.class));
    }
}