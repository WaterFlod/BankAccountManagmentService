package com.bank.account.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequest {

    @NotNull(message = "Сумма должна быть указана")
    @Positive(message = "Сумма должна быть больше нуля")
    private BigDecimal amount;

    @Size(max = 100, message = "Размер описание не должен быть больше {max} символов")
    private String description;
}
