# 🏦 Bank Account Management Service

Микросервис для управления банковскими счетами с использованием современных технологий Java-стека.

## ✨ Особенности

- ✅ **RESTful API** для операций со счетами и транзакциями
- ✅ **Транзакционная безопасность** с поддержкой ACID
- ✅ **Асинхронная обработка** через Apache Kafka
- ✅ **Полноценная база данных** PostgreSQL
- ✅ **Готовая инфраструктура** через Docker Compose

## 🛠 Технологический стек

| Компонент | Технология                     | Назначение |
|-----------|--------------------------------|------------|
| **Backend** | Java 17, Spring Boot 3.x       | Основной фреймворк |
| **База данных** | PostgreSQL 15, Spring Data JPA | Хранение данных |
| **Очередь сообщений** | Apache Kafka, Spring Kafka     | Асинхронная обработка |
| **Утилиты** | Lombok                         | Уменьшение boilerplate кода |
| **Контейнеризация** | Docker, Docker Compose         | Развертывание инфраструктуры |

## 📋 Требования

- **Java 17** или выше
- **Maven 3.8+** или **Gradle 7+**
- **Docker** и **Docker Compose** (для инфраструктуры)
- **Git** для контроля версий

## 🚀 Быстрый старт

### 1. Клонирование репозитория

```bash
git clone https://github.com/ваш-username/bank-account-service.git
cd bank-account-service
```

### 2. Запуск приложения

```bash
# Запуск 
docker-compose up -d

# Проверка запущенных контейнеров
docker-compose ps
```

Приложение будет доступно по адресу: http://localhost:8080

## 📊 База данных

### Схема данных

```sql
-- Основные таблицы
accounts          -- Информация о счетах
├── id (PK)
├── account_number (UNIQUE)
├── owner_name
├── balance
├── type (CHECKING/SAVINGS/CREDIT)
└── created_at

transactions      -- История транзакций
├── id (PK, UUID)
├── account_id (FK)
├── amount
├── type (DEPOSIT/WITHDRAWAL/TRANSFER)
├── description
└── timestamp
```

## 🔧 API Endpoints

### Управление счетами

| Метод | Endpoint | Описание |
|-------|----------|----------|
| `POST` | `/api/accounts` | Создание нового счета |
| `GET` | `/api/accounts/{accountNumber}` | Получение информации о счете |
| `GET` | `/api/accounts/{accountNumber}/transactions` | История транзакций счета |

### Операции со счетами

| Метод | Endpoint | Описание |
|-------|----------|----------|
| `POST` | `/api/accounts/{accountNumber}/deposit` | Пополнение счета |
| `POST` | `/api/accounts/{accountNumber}/withdraw` | Снятие со счета |
| `POST` | `/api/accounts/transfer` | Перевод между счетами |

## 📝 Примеры использования API

### Создание счета

```bash
curl -X POST http://localhost:8080/api/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "ownerName": "Иван Иванов",
    "type": "CHECKING",
    "initialDeposit": 5000.00
  }'
```

**Ответ:**
```json
{
  "id": 1,
  "accountNumber": "ACC123456789012",
  "ownerName": "Иван Иванов",
  "balance": 5000.00,
  "type": "CHECKING"
}
```

### Пополнение счета

```bash
curl -X POST http://localhost:8080/api/accounts/ACC123456789012/deposit \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 1500.50,
    "description": "Зарплата"
  }'
```

### Перевод между счетами

```bash
curl -X POST http://localhost:8080/api/accounts/transfer \
  -H "Content-Type: application/json" \
  -d '{
    "fromAccountNumber": "ACC123456789012",
    "toAccountNumber": "ACC987654321098",
    "amount": 1000.00,
    "description": "Оплата услуг"
  }'
```

**Ответ:**
```json
{
  "fromTransaction": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "accountNumber": "ACC123456789012",
    "amount": 1000.00,
    "type": "TRANSFER_OUT",
    "description": "Перевод на счет ACC987654321098",
    "balanceAfter": 5500.50,
    "timestamp": "2024-01-15T14:30:00"
  },
  "toTransaction": {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "accountNumber": "ACC987654321098",
    "amount": 1000.00,
    "type": "TRANSFER_IN",
    "description": "Перевод со счета ACC123456789012",
    "balanceAfter": 2000.00,
    "timestamp": "2024-01-15T14:30:00"
  },
  "transferAmount": 1000.00,
  "fee": 10.00,
  "totalDebitAmount": 1010.00,
  "status": "SUCCESS",
  "timestamp": "2024-01-15T14:30:00",
  "transferId": "TRF-1705321800000-ABC123DE"
}
```

## 🧪 Тестирование

### Запуск тестов

```bash
# Все тесты
mvn test

# Только unit-тесты
mvn test -Dtest="*UnitTest"

# Только интеграционные тесты
mvn test -Dtest="*IntegrationTest"

# С генерацией отчета о покрытии
mvn test jacoco:report
```

## 📁 Структура проекта

```
src/main/java/com/bank/account/
├── controller/         # REST контроллеры
│   ├── AccountController.java
│   └── HealthController.java
├── service/           # Бизнес-логика
│   ├── AccountService.java
│   └── TransactionService.java
├── repository/        # Доступ к данным
│   ├── AccountRepository.java
│   └── TransactionRepository.java
├── model/             # Сущности JPA
│   ├── Account.java
│   └── Transaction.java
├── dto/               # Data Transfer Objects
│   ├── AccountDTO.java
│   ├── TransactionDTO.java
│   └── TransferResultDTO.java
├── kafka/             # Работа с Kafka
│   ├── KafkaProducerConfig.java
│   ├── KafkaTransactionProducer.java
│   └── TransactionEvent.java
└── config/            # Конфигурации
    ├── SwaggerConfig.java
    └── DatabaseConfig.java

src/main/resources/
├── application.yml          # Основная конфигурация
```

## 🔐 Безопасность

### Валидация данных
- Проверка баланса перед снятием
- Валидация входных параметров через Bean Validation
- Защита от SQL-инъекций через JPA

### Транзакционная безопасность
- Использование `@Transactional` для операций
- Оптимистичная блокировка через `@Version`

### Логирование
Уровни логирования настраиваются через `application.yml`:
```yaml
  logging:
    level:
      com.example.demo: DEBUG
      org.springframework.kafka: INFO
      org.hibernate.SQL: DEBUG
      org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

## 🔄 Работа с Kafka

### Отправка событий
При каждой транзакции отправляется событие в Kafka:

```json
{
  "transactionId": "550e8400-e29b-41d4-a716-446655440000",
  "accountNumber": "ACC123456789012",
  "amount": 1000.00,
  "type": "DEPOSIT",
  "description": "Зарплата",
  "balanceAfter": 6000.00,
  "timestamp": "2024-01-15T14:30:00"
}
```

### Просмотр событий
```bash
# Просмотр событий в топике banking-transactions
docker exec -it kafka-broker \
  kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic banking-transactions \
  --from-beginning
```

## 🐛 Устранение неполадок

### Распространенные проблемы

1. **База данных не доступна**
   ```bash
   # Проверьте статус контейнера PostgreSQL
   docker-compose ps postgres
   
   # Проверьте логи
   docker-compose logs postgres
   ```

2. **Kafka не работает**
   ```bash
   # Проверьте, что Zookeeper запущен
   docker-compose ps zookeeper
   
   # Проверьте топики
   docker exec -it kafka-broker kafka-topics --list --bootstrap-server localhost:9092
   ```

3. **Приложение не запускается**
   ```bash
   # Проверьте порты
   netstat -tuln | grep 8080
   ```

## 📞 Контакты

Матвей Андреевич - [@waterflod](https://t.me/waterflod) - mse25019@gmail.com

Ссылка на проект: [https://github.com/WaterFlod/BankAccountManagmentService](https://github.com/WaterFlod/BankAccountManagmentService)

---

<div align="center">

### ⭐ Если проект был полезен, поставьте звезду на GitHub!

</div>