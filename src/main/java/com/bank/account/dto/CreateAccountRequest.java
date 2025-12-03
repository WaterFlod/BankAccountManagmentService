package com.bank.account.dto;

import jakarta.validation.constraints.*;
import com.bank.account.model.Account.AccountType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateAccountRequest {

    @NotBlank
    private String ownerName;

    @NotNull
    private AccountType type;

    @PositiveOrZero
    private BigDecimal initialDeposit = BigDecimal.ZERO;
}
