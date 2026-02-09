package com.bank.account.exception;

import com.bank.account.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    public ResponseEntity<ErrorResponse> handleBaseException(
            BaseException ex,
            HttpServletRequest request) {

        log.warn("Business exception: {} - {}", ex.getErrorCode(), ex.getMessage());

        Map<String, Object> details = new HashMap<>();
        if (ex instanceof InsufficientFundsException insufficientFundsException) {
            details.put("accountNumber", insufficientFundsException.getAccountNumber());
            details.put("currentBalance", insufficientFundsException.getCurrentBalance());
            details.put("requestedAmount", insufficientFundsException.getRequestedAmount());
            details.put("deficiency", insufficientFundsException.getDeficiency());
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(ex.getHttpStatus().value())
                .error(ex.getErrorCode())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .errorCode(ex.getErrorCode())
                .details(!details.isEmpty() ? details : null)
                .build();

        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }
}
