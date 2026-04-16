package com.bank.account.repository;

import com.bank.account.model.Account;
import com.bank.account.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    List<Transaction> findByAccountOrderByTimestamp(Account account);

    List<Transaction> findByAccountNumber(String accountNumber);
}
