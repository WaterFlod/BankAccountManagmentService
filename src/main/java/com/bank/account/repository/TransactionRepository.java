package com.bank.account.repository;

import com.bank.account.model.Account;
import com.bank.account.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    List<Transaction> findByAccountOrderByTimestamp(Account account);

    Page<Transaction> findByAccount(Account account, Pageable pageable);

    List<Transaction> findByType(Transaction.TransactionType type);

    List<Transaction> findByAccountAndType(
            Account account,
            Transaction.TransactionType type
    );
}
