package com.bank.account.controller;

import com.bank.account.dto.*;
import com.bank.account.security.SecurityUtils;
import com.bank.account.service.AccountService;
import com.bank.account.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;
    private final SecurityUtils secUtils;

    @GetMapping
    public String getAccounts(Authentication auth, Model model) {
        String userId = secUtils.getCurrentUserId();

        List<AccountResponse> accounts = accountService.getAllAccounts(userId);
        List<TransactionResponse> top10Transactions = transactionService.getLastTransaction(userId);

        BigDecimal amount = accounts.stream()
                .map(AccountResponse::balance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("accounts", accounts);
        model.addAttribute("transactions", top10Transactions);
        model.addAttribute("username", auth.getName());
        model.addAttribute("amount", amount);

        return "account";
    }

    @PostMapping("/open-account")
    public String createAccount() {
        return "redirect:/open-account";
    }

    @GetMapping("/{accountNumber}/deposit")
    public String depositForm(@PathVariable("accountNumber") String accountNumber,
                              Authentication auth,
                              Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("accountNumber", accountNumber);
        model.addAttribute("request", new TransactionRequest());
        return "deposit";
    }

    @PostMapping("/{accountNumber}/deposit")
    public String deposit(@PathVariable("accountNumber") String accountNumber,
                          @Valid @ModelAttribute("request") TransactionRequest request) {
        accountService.deposit(accountNumber, request);
        return "redirect:/account";
    }
}
