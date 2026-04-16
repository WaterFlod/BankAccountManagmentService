package com.bank.account.dto;

import com.bank.account.model.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse (
    String accountNumber,
    BigDecimal amount,
    TransactionType type,
    LocalDateTime timestamp,
    BigDecimal balanceAfter
){}
