package com.bank.account.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.bank.account.exception.AuthenticationException;

@Component
public class SecurityUtils {

    public String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("Пользователь не авторизован");
        }

        Object principal = authentication.getPrincipal();

        return ((UserDetailsImpl) principal).getId();



    }
}
