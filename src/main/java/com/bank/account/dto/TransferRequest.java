package com.bank.account.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {

    @NotBlank(message = "Счёт отправителя должен быть указан")
    private String fromAccountNumber;

    @NotBlank(message = "Счёт получателя должен быть указан")
    private String toAccountNumber;

    @NotNull(message = "Сумма должна быть указана")
    @Positive(message = "Сумма должна быть больше нуля")
    private BigDecimal amount;

    private String description = "Transfer";
}
