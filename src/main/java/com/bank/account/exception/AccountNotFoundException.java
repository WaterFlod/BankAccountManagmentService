package com.bank.account.exception;

import org.springframework.http.HttpStatus;

public class AccountNotFoundException extends BaseException {
    public AccountNotFoundException(String accountNumber) {
        super("ACC_NOT_FOUND", HttpStatus.NOT_FOUND,
                "Account with account number " + accountNumber + " not found");
    }

    public AccountNotFoundException(String accountNumber, Throwable cause) {
        super("ACC_NOT_FOUND", HttpStatus.NOT_FOUND,
                "Account with account number " + accountNumber + " not found", cause);
    }
}
