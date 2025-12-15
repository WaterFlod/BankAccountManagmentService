package com.bank.account.repository;

import com.bank.account.model.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // Найти по номеру счёта
    Optional<Account> findByAccountNumber(String accountNumber);

    // Найти по имени владельца
    List<Account> findByOwnerName(String ownerName);

    // Найти по типу счёта
    List<Account> findByType(Account.AccountType type);
}
