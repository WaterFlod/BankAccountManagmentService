package com.bank.account.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public class ValidationException extends BaseException{

    private final List<String> errors;

    public ValidationException(String message, List<String> errors) {
        super("VALIDATION_ERROR", HttpStatus.BAD_REQUEST, message);
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
