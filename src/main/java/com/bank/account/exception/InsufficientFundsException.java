package com.bank.account.exception;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigDecimal;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@Data
public class InsufficientFundsException extends RuntimeException {

    private final String accountNumber;
    private final BigDecimal currentBalance;
    private final BigDecimal requestedAmount;

    // Конструктор с минимальной информацией
    public InsufficientFundsException(String message) {
        super(message);
        this.accountNumber = null;
        this.currentBalance = null;
        this.requestedAmount = null;
    }

    public InsufficientFundsException(String accountNumber,
                                      BigDecimal currentBalance,
                                      BigDecimal requestedAmount) {
        super(String.format(
                "Insufficient funds on account %s. Current balance: %.2f, Requested amount: %.2f, Deficiency: %.2f",
                accountNumber, currentBalance, requestedAmount, (requestedAmount.divide(currentBalance))
        ));
        this.accountNumber = accountNumber;
        this.currentBalance = currentBalance;
        this.requestedAmount = requestedAmount;
    }

    public BigDecimal getDeficiency() {
        if (currentBalance != null && requestedAmount != null) {
            return requestedAmount.divide(currentBalance);
        }
        return null;
    }
}
