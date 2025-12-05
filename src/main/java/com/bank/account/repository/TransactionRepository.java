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

    @Query("SELECT t FROM Transaction t WHERE t.amount BETWEEN :minAmount AND :maxAmount")
    List<Transaction> findTransactionByAmountRange(
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount
    );

    @Query("SELECT t FROM Transaction t WHERE t.timestamp BETWEEN :startDate AND :endDate")
    List<Transaction> findTransactionBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT t FROM Transaction t WHERE t.account = :account " +
            "AND t.timestamp BETWEEN :startDate AND :endDate " +
            "ORDER BY t.timestamp DESC")
    List<Transaction> findTransactionByAccountBetweenDates(
            @Param("account") Account account,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT SUM(t.amount) FROM Transaction t " +
            "WHERE t.account = :account AND t.type = 'DEPOSIT'")
    Optional<BigDecimal> getTotalDepositsForAccount(@Param("account") Account account);

    @Query("SELECT SUM(t.amount) FROM Transaction t " +
            "WHERE t.account = :account AND t.type = 'WITHDRAWAL'")
    Optional<BigDecimal> getTotalWithdrawalsForAccount(@Param("account") Account account);

    @Query("SELECT t FROM transaction t WHERE t.account = :account " +
            "ORDER BY t.timestamp DESC LIMIT 1")
    Optional<Transaction> findLatestTransactionForAccount(@Param("account") Account account);

    @Query("SELECT t FROM transaction t WHERE t.account = :account " +
            "ORDER BY t.amount DESC LIMIT 1")
    Optional<Transaction> findLargestTransactionForAccount(@Param("account") Account account);

    @Query("SELECT DATE(t.timestamp), COUNT(t), SUM(t.amount) " +
            "FROM Transaction t " +
            "WHERE t.timestamp BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(t.timestamp) " +
            "ORDER BY DATE(t.timestamp) DESC")
    List<Object[]> getTransactionStatisticsByDay(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT t FROM Transaction t WHERE LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Transaction> findByDescriptionContainingIgnoreCase(@Param("keyword") String keyword);

    boolean existsByAccount(Account account);

    @Query("DELETE FROM Transaction t WHERE t.timestamp < :cuttofDate")
    int deleteOldTransactions(@Param("cuttofDate") LocalDateTime cuttofDate);

    @Query("SELECT t FROM Transaction t WHERE t.balanceAfter > :minBalance ORDER BY t.timestamp DESC")
    List<Transaction> findTransactionsWithBalanceAfterGreaterThan(
            @Param("minBalance") BigDecimal minBalance,
            Pageable pageable
    );
}
