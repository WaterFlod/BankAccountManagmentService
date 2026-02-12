package com.bank.account.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(
        @NotBlank @Email @Size(max=254) String email,
        @NotBlank @Size(min=8, max=64) String password,
        @NotBlank @Size(max=64) String firstName,
        @NotBlank @Size(max=64) String lastName
) {}
