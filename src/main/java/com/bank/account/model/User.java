package com.bank.account.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    @ToString.Include
    @Setter(AccessLevel.NONE)
    private String id;

    @Column(unique = true, nullable = false)
    @EqualsAndHashCode.Include
    @Setter(AccessLevel.NONE)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;
}
