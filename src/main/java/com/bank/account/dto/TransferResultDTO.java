package com.bank.account.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransferResultDTO {
    private TransactionDTO fromTransaction;
    private TransactionDTO toTransaction;
    private BigDecimal transferAmount;
}
