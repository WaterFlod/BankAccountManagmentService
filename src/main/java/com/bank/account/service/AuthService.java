package com.bank.account.service;

import com.bank.account.dto.RegistrationRequest;
import com.bank.account.dto.UserDTO;
import com.bank.account.model.User;
import com.bank.account.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    @Transactional
    public UserDTO createUser(RegistrationRequest requset) {
        User user = User.builder()
                .email(requset.email())
                .password(requset.password())
                .firstName(requset.firstName())
                .lastName(requset.lastName())
                .build();

        user = userRepository.save(user);

        log.info("User registred: {}", user.getId());
        return convertToDTO(user);
    }

    private UserDTO convertToDTO(User user) {

        return UserDTO.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }
}