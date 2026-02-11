package com.bank.account.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransactionRequest {

    @NotNull(message = "Сумма должна быть указана")
    @Positive(message = "Сумма должна быть больше нуля")
    private BigDecimal amount;

    @Size(max = 100, message = "Размер описание не должен быть больше {max} символов")
    private String description;
}
