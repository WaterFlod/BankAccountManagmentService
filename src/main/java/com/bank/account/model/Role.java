package com.bank.account.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

public enum Role {
    USER, ADMIN
}
