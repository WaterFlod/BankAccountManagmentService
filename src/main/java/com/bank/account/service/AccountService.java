package com.bank.account.service;

import com.bank.account.dto.*;
import com.bank.account.model.*;
import com.bank.account.repository.CreditAccountRepository;
import com.bank.account.repository.SavingsAccountRepository;
import com.bank.account.exception.AccountNotFoundException;
import com.bank.account.exception.InsufficientFundsException;
import com.bank.user.model.User;
import com.bank.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
// Business logic of accounts
public class AccountService {
    private final AccountRepository accountRepository;
    private final SavingsAccountRepository savingsAccountRepository;
    private final CreditAccountRepository creditAccountRepository;
    private final TransactionService transactionService;

    private static final BigDecimal SAVINGS_RATE = new BigDecimal("0.05");
    private static final BigDecimal CREDIT_RATE = new BigDecimal("0.20");
    private static final BigDecimal DAYS_IN_YEAR = new BigDecimal("365");
    private static final MathContext MC = new MathContext(10, RoundingMode.HALF_UP);

    @Transactional
    public CheckingAccount createCheckingAccount(User user, BigDecimal initialBalance) {
        if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("The initial deposit must not be negative");
        }

        CheckingAccount account = new CheckingAccount(generateAccountNumber(), initialBalance, user);

        /* The general logic for creating accounts has been moved
           to a separate method to reduce the amount of code */
        account = (CheckingAccount) createAccount(account, initialBalance);

        log.info("Create checking account {} for the user {}", account.getAccountNumber(), user.getEmail());

        return account;
    }

    @Transactional
    public SavingsAccount createSavingsAccount(User user, BigDecimal initialBalance) {
        if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("The initial deposit must not be negative");
        }

        SavingsAccount account = new SavingsAccount(generateAccountNumber(), initialBalance, user);

        /* The general logic for creating accounts has been moved
           to a separate method to reduce the amount of code */
        account = (SavingsAccount) createAccount(account, initialBalance);

        log.info("Create savings account {} for the user {}", account.getAccountNumber(), user.getEmail());

        return account;
    }

    @Transactional
    public CreditAccount createCreditAccount(User user, BigDecimal creditAmount, BigDecimal creditLimit) {
        if (creditAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("The credit amount must not be negative");
        }

        CreditAccount account = new CreditAccount(generateAccountNumber(), creditAmount, creditLimit, user);

        /* The general logic for creating accounts has been moved
           to a separate method to reduce the amount of code */
        account = (CreditAccount) createAccount(account, creditAmount);

        log.info("Create credit account {} with a limit {} for the user {}",
                account.getAccountNumber(), creditLimit, user.getEmail());

        return account;
    }

    @Transactional(readOnly = true)
    public List<Account> getAllAccounts(String userId) {
        return accountRepository.findByUserId(userId);
    }

    @Transactional(propagation = Propagation.NEVER)
    public void deposit(String accountNumber, BigDecimal amount) {
        int retries = 3;
        while (retries-- > 0) {
            try {
                depositInternal(accountNumber, amount);
                return;
            } catch (OptimisticLockingFailureException e) {
                if (retries == 0) throw e;
                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
            }
        }
    }

    @Transactional
    public void depositInternal(String accountNumber, BigDecimal amount) {
        Account account = getAccount(accountNumber);

        account.deposit(amount);
        accountRepository.save(account);

        transactionService.createTransaction(account, amount, TransactionType.DEPOSIT,
                "Deposit account " + accountNumber + " for the amount " + amount, account.getBalance());

        log.info("Deposit account {} for the amount {}: new balance {}", accountNumber, amount, account.getBalance());
    }

    @Transactional(propagation = Propagation.NEVER)
    public void withdraw(String accountNumber, BigDecimal amount) {
        int retries = 3;
        while (retries-- > 0) {
            try {
                withdrawInternal(accountNumber, amount);
                return;
            } catch (OptimisticLockingFailureException e) {
                if (retries == 0) throw e;
                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
            }
        }
    }

    @Transactional
    public void withdrawInternal(String accountNumber, BigDecimal amount) {
        Account account = getAccount(accountNumber);

        account.withdraw(amount);

        accountRepository.save(account);

        transactionService.createTransaction(account, amount, TransactionType.WITHDRAWAL,
                "Withdraw for the account " + account.getAccountNumber() + " for the amount " + amount, newBalance);

        log.info("Withdraw for the account {} for the amount {}: new balance {}", accountNumber, amount, newBalance);
    }

    @Transactional(propagation = Propagation.NEVER)
    public void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        int retries = 3;
        while (retries-- > 0) {
            try {
                transferInternal(fromAccountNumber, toAccountNumber, amount);
                return;
            } catch (OptimisticLockingFailureException e) {
                if (retries == 0) throw e;
                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
            }
        }
    }

    @Transactional
    public void transferInternal(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new IllegalArgumentException("Transfers to the same account cannot be made");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        Account from = getAccount(fromAccountNumber);
        Account to = getAccount(toAccountNumber);

        from.withdraw(amount);
        to.deposit(amount);

        accountRepository.save(from);
        accountRepository.save(to);

        transactionService.createTransaction(from, amount, TransactionType.TRANSFER_OUT,
                "Transfer to account " + toAccountNumber, from.getBalance());

        transactionService.createTransaction(to, amount, TransactionType.TRANSFER_IN,
                "Transfer from account " + fromAccountNumber, to.getBalance());

        log.info("Transfer {} from account {} to account {} completed",
                amount, fromAccountNumber, toAccountNumber);
    }

    @Transactional
    public void applyInterestToSavings() {
        LocalDate today = LocalDate.now();
        List<SavingsAccount> accounts = savingsAccountRepository.findByLastInterestDateBefore(today);

        for (SavingsAccount sa: accounts) {
            long days = ChronoUnit.DAYS.between(sa.getLastInterestDate(), today);
            if (days <= 0) continue;

            BigDecimal dailyRate = SAVINGS_RATE.divide(DAYS_IN_YEAR, MC);
            BigDecimal interest = sa.getBalance()
                    .multiply(dailyRate)
                    .multiply(BigDecimal.valueOf(days))
                    .setScale(2, RoundingMode.HALF_UP);

            if (interest.compareTo(BigDecimal.ZERO) > 0) {
                sa.deposit(interest);
                sa.setLastInterestDate(today);
                savingsAccountRepository.save(sa);

                transactionService.createTransaction(sa, interest, TransactionType.INTEREST,
                        "Начислены проценты за " + days + " дн.", sa.getBalance());
                log.info("Начислены проценты {} на счет {}", interest, sa.getAccountNumber());
            }
        }
    }

    @Transactional
    public void applyInterestToCredit() {
        LocalDate today = LocalDate.now();
        List<CreditAccount> accounts = creditAccountRepository.findByLastInterestDateBefore(today);

        for (CreditAccount ca: accounts) {
            long days = ChronoUnit.DAYS.between(ca.getLastInterestDate(), today);
            if (days <= 0 || ca.getPrincipalDebit().compareTo(BigDecimal.ZERO) == 0) continue;

            BigDecimal dailyRate = CREDIT_RATE.divide(DAYS_IN_YEAR, MC);
            BigDecimal interest = ca.getPrincipalDebit()
                    .multiply(dailyRate)
                    .multiply(BigDecimal.valueOf(days))
                    .setScale(2, RoundingMode.HALF_UP);

            if (interest.compareTo(BigDecimal.ZERO) > 0) {
                ca.setAccruedInterest(ca.getAccruedInterest().add(interest));
                BigDecimal newBalance = ca.getBalance().subtract(interest);
                ca.setBalance(newBalance);
                ca.setLastInterestDate(today);
                creditAccountRepository.save(ca);

                transactionService.createTransaction(ca, interest, TransactionType.INTEREST,
                        "Начислены проценты по кредиту за " + days + " дн.", newBalance);
                log.info("Начислены проценты по кредиту {} на сумму {}", ca.getAccountNumber(), interest);
            }
        }
    }

    private Account createAccount(Account account, BigDecimal initialBalance) {
        account = accountRepository.save(account);

        // If the initial deposit is not positive, the transaction should not be created
        if (initialBalance.compareTo(BigDecimal.ZERO) > 0) {
            transactionService.createTransaction(account, initialBalance, TransactionType.DEPOSIT,
                    "Initial deposit", account.getBalance());
        }

        return account;
    }

    private Account getAccount(String accountNumber) {
            return accountRepository.findByAccountNumber(accountNumber)
                    .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    private String generateAccountNumber() {
        String number;
        do {
            number = UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase();
        } while (accountRepository.existsByAccountNumber(number));
        return number;
    }
}
