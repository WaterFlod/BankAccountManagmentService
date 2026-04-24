package com.bank.account.controller;

import com.bank.account.dto.*;
import com.bank.account.model.AccountType;
import com.bank.account.security.SecurityUtils;
import com.bank.account.service.AccountService;
import com.bank.account.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
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

    @GetMapping("/open-account")
    public String createAccountForm(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("request", new OpenAccount());
        return "open-account";
    }

    @PostMapping("/open-account")
    public String createAccount(@Valid @ModelAttribute("request") OpenAccount request,
                                Authentication auth) {

        BigDecimal amount = request.getAmount();

        CreateAccountRequest createRequest = new CreateAccountRequest(
                auth.getName(),
                AccountType.CHECKING,
                amount
        );

        accountService.createAccount(createRequest);

        return "redirect:/account";
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

    @GetMapping("/{accountNumber}/transfer")
    public String transferForm(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("request", new TransferRequest());

        return "transfer";
    }

    @PostMapping("/{accountNumber}/transfer")
    public String transfer(@PathVariable("accountNumber") String accountNumber,
                           @Valid @ModelAttribute("request") TransferRequest request,
                           Authentication auth, Model model) {
        request.setFromAccountNumber(accountNumber);

        accountService.transfer(request);

        return "redirect:/account";
    }

    @GetMapping("/{accountNumber}/transaction")
    public String getAccountTransactions(@PathVariable("accountNumber") String accountNumber,
                                         Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());

        List<TransactionResponse> transactions = transactionService.getAccountTransaction(accountNumber);

        model.addAttribute("transactions", transactions);

        return "transaction";
    }
}
