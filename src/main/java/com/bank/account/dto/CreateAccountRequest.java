package com.bank.account.dto;

import com.bank.account.model.Account.AccountType;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;


import java.math.BigDecimal;

@Data
@Builder
public class CreateAccountRequest {

    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 50, message = "Размер имени ")
    private String ownerName;

    @NotNull(message = "Тип обязателен")
    private AccountType type;

    @PositiveOrZero(message = "Депозит не может быть отрицательным")
    private BigDecimal initialDeposit;
}
