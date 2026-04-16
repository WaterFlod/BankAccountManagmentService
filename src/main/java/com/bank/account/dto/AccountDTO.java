package com.bank.account.dto;

import com.bank.account.model.AccountType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountDTO {
    private String id;
    private String accountNumber;
    private BigDecimal balance;
    private AccountType type;
}
