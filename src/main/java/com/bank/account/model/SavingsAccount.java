package com.bank.account.model;

import com.bank.user.model.User;
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
@DiscriminatorValue("SAVINGS")
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
public class SavingsAccount extends Account {

    private LocalDate lastInterestDate;

    public SavingsAccount(String accountNumber, BigDecimal initialBalance, User user) {
        super(accountNumber, initialBalance, user);
        this.lastInterestDate = LocalDate.now();
    }

    public BigDecimal applyInterest(LocalDate today, BigDecimal annualRate) {
        if (today == null || annualRate == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }
        if (today.isBefore(lastInterestDate)) {
            return BigDecimal.ZERO;
        }
        long days = ChronoUnit.DAYS.between(lastInterestDate, today);
        if (days <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal dailyRate = annualRate.divide(BigDecimal.valueOf(365), 10, RoundingMode.HALF_UP);
        BigDecimal interest = getBalance()
                .multiply(dailyRate)
                .multiply(BigDecimal.valueOf(days))
                .setScale(2, RoundingMode.HALF_UP);

        if (interest.compareTo(BigDecimal.ZERO) > 0) {
            deposit(interest);
            this.setLastInterestDate(today);
        }
        return interest;
    }
}
