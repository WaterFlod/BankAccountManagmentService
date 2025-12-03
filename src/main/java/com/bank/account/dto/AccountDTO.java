package com.bank.account.dto;

import com.bank.account.model.Account.AccountType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountDTO {
    private Long id;
    private String accountNumber;
    private String ownerName;
    private BigDecimal balance;
    private AccountType type;
}
