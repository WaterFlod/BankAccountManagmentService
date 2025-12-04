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

    // Поиск счетов с балансом выше указанного лимита
    @Query("SELECT a FROM Account a WHERE a.balance > :minBalance")
    List<Account> findAccountsWithBalanceGreaterThan(@Param("minBalance") BigDecimal minBalance);

    // Поиск счетов с балансом в указанном диапазоне
    @Query("SELECT a FROM Account a WHERE a.balance BETWEEN :minBalance AND :maxBalance")
    List<Account> findAccountsByBalanceRange(
            @Param("minBalance") BigDecimal minBalance,
            @Param("maxBalance") BigDecimal maxBalance
    );

    // Получение Общего баланса всех счетов
    @Query("SELECT SUM(a.balance) FROM Account a")
    Optional<BigDecimal> getTotalBalance();

    // Получение счёта по номеру счёта с пессимистичной блокировкой
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a a.accountNumber = :accountNumber")
    Optional<Account> findByAccountNumberWithLock(@Param("accountNumber") String accountNumber);

    // Проверка существования счёта по номеру
    boolean existByAccountNumber(String accountNumber);

    // Поиск счёта по части имени владельца (регистронезависимый)
    @Query("SELECT a FROM Account a WHERE LOWER(a.ownerName) LIKE LOWER(CONCAT('%', :namePart, '%'))")
    List<Account> findByOwnerNameContainingIgnoreCase(@Param("namePart") String namePart);

    /* Получение статистики по типам счетов
    *  Возвращает количество счетов и средний баланс для каждого типа
    * */
    @Query("SELECT a.type, COUNT(a), AVG(a.balance) FROM Account a GROUP BY a.type")
    List<Object[]> getAccountStatisticsByType();

    // Удаление счёта по номеру (возвращает количество удалённых записей)
    int deleteByAccountNumber(String accountNumber);

    // Поиск счетов созданных после указанной даты
    List<Account> findByCreatedAtAfter(LocalDateTime date);
}
