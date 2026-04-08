package com.bank.account.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegistrationRequest {

    @NotBlank(message = "Введите email")
    @Email(message = "Email некорректен — проверьте формат, например: name@example.com")
    private String email;

    @NotBlank(message = "Придумайте пароль")
    @Size(min = 8, message = "Пароль должен быть минимум 8 символов")
    private String password;

    @NotBlank(message = "Подтвердите пароль")
    @Size(min = 8, message = "Пароль должен быть минимум 8 символов")
    private String confirmPassword;

    @NotBlank(message = "Укажите номер телефона")
    @Pattern(regexp = "^((8|\\+7|7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$", message = "Номер телефона некоректен")
    private String phoneNumber;

    @NotBlank(message = "Введите имя")
    @Size(max = 256, message = "Имя не может быть больше 256 символов")
    private String firstName;

    @NotBlank(message = "Введите фамилию")
    @Size(max = 256, message = "Фамилия не может быть больше 256 символов")
    private String lastName;
}
