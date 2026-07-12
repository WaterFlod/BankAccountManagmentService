package com.bank.account.model;

import com.bank.account.exception.InsufficientFundsException;
import com.bank.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "accounts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter
public abstract class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    @Setter(AccessLevel.PROTECTED)
    private BigDecimal balance = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Version
    @Setter(AccessLevel.NONE)
    private Long version;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Account(String accountNumber, BigDecimal initialBalance, User user) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        this.user = user;
    }

    public BigDecimal deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        setBalance(balance.add(amount));

        return balance;
    }

    public BigDecimal withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Withdraw amount must be positive");
        }
        if (amount.compareTo(balance) < 0) {
            throw new InsufficientFundsException("Insufficient funds in the account");
        }

        setBalance(balance.subtract(amount));

        return balance;
    }
}
