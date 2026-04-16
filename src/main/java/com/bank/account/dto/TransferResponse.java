package com.bank.account.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferResponse (
        String fromAccountNumber,
        String toAccountNumber,
        BigDecimal amount,
        BigDecimal fromBalanceAfter,
        LocalDateTime timestamp
) {}
