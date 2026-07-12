package com.bank.account.model;

import com.bank.account.exception.InsufficientFundsException;
import com.bank.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@DiscriminatorValue("CREDIT")
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
public class CreditAccount extends Account {

    @Column(nullable = false)
    private BigDecimal principalDebit;

    @Column(nullable = false)
    private BigDecimal creditLimit;

    @Column(nullable = false)
    private BigDecimal accruedInterest;

    @Column
    private LocalDate lastInterestDate;

    public CreditAccount(String accountNumber, BigDecimal creditAmount, BigDecimal creditLimit, User user) {
        super(accountNumber, creditAmount.negate(), user);
        this.creditLimit = creditLimit;
        this.principalDebit = creditAmount;
        this.accruedInterest = BigDecimal.ZERO;
        this.lastInterestDate = LocalDate.now();
    }

    @Override
    public BigDecimal deposit(BigDecimal amount) {
        if (principalDebit.compareTo(amount) <= 0) {
            setPrincipalDebit(BigDecimal.ZERO);
        } else {
            setPrincipalDebit(principalDebit.subtract(amount));
        }

        return super.deposit(amount);
    }

    @Override
    public BigDecimal withdraw(BigDecimal amount) {
        BigDecimal used = principalDebit.add(accruedInterest);
        BigDecimal available = creditLimit.subtract(used);
        if (amount.compareTo(available) > 0) {
            throw new InsufficientFundsException("Insufficient credit limit. Available: " + available);
        }

        setPrincipalDebit(principalDebit.add(amount));

        return super.withdraw(amount);
    }
}
