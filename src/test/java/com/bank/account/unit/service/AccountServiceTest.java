package com.bank.account.unit.service;

import com.bank.account.dto.AccountDTO;
import com.bank.account.dto.CreateAccountRequest;
import com.bank.account.dto.TransactionRequest;
import com.bank.account.model.Account;
import com.bank.account.repository.AccountRepository;
import com.bank.account.service.AccountService;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private Account testAccount;

    @Mock
    private TransactionRequest transactionRequest;

    @Mock
    AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private CreateAccountRequest createValidRequest() {
        return CreateAccountRequest.builder()
                .ownerName("John Doe")
                .type(Account.AccountType.SAVINGS)
                .initialDeposit(new BigDecimal("500"))
                .build();
    }

    @DisplayName("Создание счёта с валидным запросом должно возвращать валидный AccountDTO")
    @Test
    void createAccountWithValidRequest_returnsValidAccountDTO() {
        CreateAccountRequest createRequest = createValidRequest();

        Account savedAccount = new Account();
        savedAccount.setId(1L);
        savedAccount.setAccountNumber("ACC123456789");
        savedAccount.setOwnerName(createRequest.getOwnerName());
        savedAccount.setType(createRequest.getType());
        savedAccount.setBalance(createRequest.getInitialDeposit());

        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);


        AccountDTO result = accountService.createAccount(createRequest);

        assertNotNull(result);
        assertTrue(result.getAccountNumber().startsWith("ACC"));
        assertEquals(result.getOwnerName(), "John Doe");
        assertEquals(result.getBalance(), new BigDecimal("500"));
        assertEquals(result.getType(), Account.AccountType.SAVINGS);

        verify(accountRepository).save(any(Account.class));
    }
}