package com.bank.account.kafka;

import com.bank.account.model.Transaction.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent {
    private String transactionId;
    private String accountNumber;
    private BigDecimal amount;
    private TransactionType type;
    private String description;
    private BigDecimal balanceAfter;
    private LocalDateTime timestamp;
}
