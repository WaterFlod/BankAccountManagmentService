package com.bank.account.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequest {

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotBlank
    private String description;
}
