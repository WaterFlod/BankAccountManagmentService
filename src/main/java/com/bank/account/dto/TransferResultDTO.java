package com.bank.account.dto;

import java.math.BigDecimal;

public class TransferResultDTO {
    private TransactionDTO fromTransaction;
    private TransactionDTO toTransaction;
    private BigDecimal transferAmount;
}
