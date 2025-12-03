package com.bank.account.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {

    @NotBlank
    private String fromAccountNumber;

    @NotBlank
    private String toAccountNumber;

    @NotNull
    @Positive
    private BigDecimal amount;

    private String description = "Transfer";
}
