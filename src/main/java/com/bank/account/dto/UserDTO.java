package com.bank.account.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
