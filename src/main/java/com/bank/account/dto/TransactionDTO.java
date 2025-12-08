package com.bank.account.dto;

import com.bank.account.model.Transaction.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDTO {
    private String id;
    private String accountNumber;
    private BigDecimal amount;
    private TransactionType type;
    private String description;
    private LocalDateTime timestamp;
    private BigDecimal balanceAfter;
}
