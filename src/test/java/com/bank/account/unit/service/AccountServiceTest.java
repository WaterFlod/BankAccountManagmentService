package com.bank.account.unit.service;

import com.bank.account.dto.AccountDTO;
import com.bank.account.dto.CreateAccountRequest;
import com.bank.account.dto.TransactionDTO;
import com.bank.account.dto.TransactionRequest;
import com.bank.account.exception.AccountNotFoundException;
import com.bank.account.kafka.KafkaTransactionProducer;
import com.bank.account.kafka.TransactionEvent;
import com.bank.account.model.Account;
import com.bank.account.model.Transaction;
import com.bank.account.repository.AccountRepository;
import com.bank.account.repository.TransactionRepository;
import com.bank.account.service.AccountService;
import jakarta.xml.bind.ValidationException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    AccountRepository accountRepository;

    @Mock
    TransactionRepository transactionRepository;

    @Mock
    KafkaTransactionProducer kafkaProducer;

    @Mock
    private TransactionRequest transactionRequest;

    private String testAccountNumber;
    private BigDecimal initialBalance;
    private Account mockAccount;

    @BeforeEach
    void setUp() {
        testAccountNumber = "ACC123";
        initialBalance = new BigDecimal("1000.00");

        mockAccount = Account.builder()
                .id(1L)
                .accountNumber(testAccountNumber)
                .ownerName("Ivan")
                .balance(initialBalance)
                .type(Account.AccountType.SAVINGS)
                .build();
    }

    @Nested
    @DisplayName("Тесты создания счета")
    class CreateAccountTest {

        @DisplayName("Создание счёта с валидным запросом должно возвращать валидный AccountDTO")
        @Test
        void createAccountWithValidRequest_returnsValidAccountDTO() {
            CreateAccountRequest createRequest = CreateAccountRequest.builder()
                    .ownerName("John Doe")
                    .type(Account.AccountType.SAVINGS)
                    .initialDeposit(new BigDecimal("500"))
                    .build();

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

    @Nested
    @DisplayName("Тесты депозитов")
    class DepositTest {

        @DisplayName("Депозит должен увеличивать баланс счёта и создавать транзакцию")
        @Test
        void deposit_shouldIncreaseAccountBalanceAndCreateTransaction() {
            BigDecimal depositAmount = new BigDecimal("500.00");
            BigDecimal expectedBalance = new BigDecimal("1500.00");

            TransactionRequest request = TransactionRequest.builder()
                    .amount(depositAmount)
                    .description("Deposit")
                    .build();


            Transaction savedTransaction = new Transaction();
            savedTransaction.setId("123");
            savedTransaction.setAccount(mockAccount);
            savedTransaction.setAmount(depositAmount);
            savedTransaction.setDescription("Deposit");
            savedTransaction.setType(Transaction.TransactionType.DEPOSIT);

            when(accountRepository.findByAccountNumber(testAccountNumber))
                    .thenReturn(Optional.of(mockAccount));

            when(accountRepository.save(any(Account.class)))
                    .thenAnswer(invocation -> {
                        Account savedAccount = invocation.getArgument(0);
                        assertThat(savedAccount.getBalance().equals(expectedBalance));
                        return savedAccount;
                    });

            when(transactionRepository.save(any(Transaction.class)))
                    .thenReturn(savedTransaction);

            TransactionDTO result = accountService.deposit(testAccountNumber, request);

            verify(accountRepository).findByAccountNumber(testAccountNumber);
            verify(accountRepository).save(mockAccount);
            verify(transactionRepository).save(any(Transaction.class));
            verify(kafkaProducer).sendTransactionEvent(any(TransactionEvent.class));

            assertThat(result).isNotNull();
            assertThat(result.getAmount()).isEqualTo(depositAmount);
            assertThat(result.getType()).isEqualTo(Transaction.TransactionType.DEPOSIT);
        }

        @DisplayName("Депозит должен бросать исключение, если счёт не найден")
        @Test
        void deposit_shouldThrowAccountNotFoundExceptionWhenAccountNotFound() {
            TransactionRequest request = TransactionRequest.builder()
                    .amount(new BigDecimal("1000.00"))
                    .description("Deposit")
                    .build();

            when(accountRepository.findByAccountNumber(testAccountNumber))
                    .thenReturn(Optional.empty());

            AccountNotFoundException exception = assertThrows(
                    AccountNotFoundException.class,
                    () -> accountService.deposit(testAccountNumber, request)
            );

            assertEquals("Account with account number " + testAccountNumber + " not found",
                    exception.getMessage());

            verify(accountRepository, never()).save(any(Account.class));
            verify(kafkaProducer, never()).sendTransactionEvent((any()));
        }
    }

    @Nested
    @DisplayName("Тесты вывода средств")
    class WithdrawTest {

        @DisplayName("Вывод средств должен уменьшать баланс счёта и создавать транзакцию")
        @Test
        void withdraw_shouldReduceAccountBalanceAndCreateTransaction() {
            BigDecimal withdrawAmount = new BigDecimal("500.00");
            BigDecimal expectedBalance = new BigDecimal("500.00");

            TransactionRequest request = TransactionRequest.builder()
                    .amount(withdrawAmount)
                    .description("Withdraw")
                    .build();

            Transaction savedTransaction = new Transaction();
            savedTransaction.setId("123");
            savedTransaction.setAccount(mockAccount);
            savedTransaction.setAmount(withdrawAmount);
            savedTransaction.setDescription("Withdraw");
            savedTransaction.setType(Transaction.TransactionType.WITHDRAWAL);

            when(accountRepository.findByAccountNumber(testAccountNumber))
                    .thenReturn(Optional.of(mockAccount));

            when(accountRepository.save(any(Account.class)))
                    .thenAnswer(invocation -> {
                        Account savedAccount = invocation.getArgument(0);
                        assertThat(savedAccount.getBalance().equals(expectedBalance));
                        return savedAccount;
                    });

            when(transactionRepository.save(any(Transaction.class)))
                    .thenReturn(savedTransaction);

            TransactionDTO result = accountService.withdraw(testAccountNumber, request);

            verify(accountRepository).findByAccountNumber(testAccountNumber);
            verify(accountRepository).save(mockAccount);
            verify(transactionRepository).save(any(Transaction.class));
            verify(kafkaProducer).sendTransactionEvent(any(TransactionEvent.class));

            assertThat(result).isNotNull();
            assertThat(result.getAmount()).isEqualTo(withdrawAmount);
            assertThat(result.getType()).isEqualTo(Transaction.TransactionType.WITHDRAWAL);
        }
    }
}