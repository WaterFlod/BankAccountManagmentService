package com.bank.account.service;

import com.bank.account.dto.RegistrationRequest;
import com.bank.account.model.Role;
import com.bank.account.model.User;
import com.bank.account.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean existsUserByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsUserByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    public User findUserByIdentifier(String identifier) {
        Optional<User> user = userRepository.findByEmail(identifier);
        return user.get();
    }

    public boolean registerUser(RegistrationRequest request) {
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .role(Role.USER)
                .build();
        userRepository.save(user);
        return true;
    }
}
