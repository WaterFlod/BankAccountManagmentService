package com.bank.account.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

@Getter
public class InsufficientFundsException extends BaseException {

    private final String accountNumber;
    private final BigDecimal currentBalance;
    private final BigDecimal requestedAmount;
    private final BigDecimal deficiency;

    public InsufficientFundsException(String accountNumber,
                                      BigDecimal currentBalance,
                                      BigDecimal requestedAmount) {
        super(
                "INSUFFICIENT_FUNDS",
                HttpStatus.BAD_REQUEST,
                String.format(
                "Insufficient funds on account %s. Current balance: %.2f, Requested amount: %.2f",
                accountNumber, currentBalance, requestedAmount
        ));
        this.accountNumber = accountNumber;
        this.currentBalance = currentBalance;
        this.requestedAmount = requestedAmount;
        this.deficiency = requestedAmount.subtract(currentBalance);
    }
}
