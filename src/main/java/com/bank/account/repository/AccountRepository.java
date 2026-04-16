package com.bank.account.repository;

import com.bank.account.model.Account;
import com.bank.account.model.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    // Найти по номеру счёта
    Optional<Account> findByAccountNumber(String accountNumber);

    // Найти по типу счёта
    List<Account> findByType(AccountType type);
}
