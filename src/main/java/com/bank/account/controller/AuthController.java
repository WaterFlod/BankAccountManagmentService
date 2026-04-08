package com.bank.account.controller;

import com.bank.account.dto.RegistrationRequest;
import com.bank.account.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private final UserService userService;

    @GetMapping("/login")
    public String loginForm(@RequestParam(value = "error", required = false) String error,
                            Model model) {
        if (error != null) {
            model.addAttribute("error", "Неверное имя пользователя или пароль");
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("request", new RegistrationRequest());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("request") RegistrationRequest request,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            return "register";
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            model.addAttribute("confirmError", "Ваши пароли не совпадают");
            return "register";
        }
        if (userService.existsUserByEmail(request.getEmail())) {
            model.addAttribute("error", "Пользователь с таким email уже существует");
            return "register";
        }
        if (userService.existsUserByPhoneNumber(request.getPhoneNumber())) {
            model.addAttribute("error", "Пользователь с таким номером телефона уже существует");
            return "register";
        }
        boolean success = userService.registerUser(request);
        return "redirect:/login?registered";
    }
}
