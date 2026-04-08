# The bank's website

Сайт банка с использованием современного Java-стека.<br>
Проект разрабатываю для изучения банковского домена изнутри.

## Технологический стек

| Компонент           | Технология                        | Назначение                         |
|---------------------|-----------------------------------|------------------------------------|
| **Backend**         | Java 25, Spring Boot 4.x          | Основной фреймворк                 |
| **Frontend**        | HTML5, CSS, JavaScript, Thymeleaf | Отображение динамичных веб-страниц |
| **База данных**     | PostgreSQL 15, Spring Data JPA    | Хранение данных                    |
| **Безопасность**    | Spring Security 6                 | Аутентификация пользователя        |
| **Утилиты**         | Lombok                            | Уменьшение boilerplate кода        |
| **Контейнеризация** | Docker, Docker Compose            | Развертывание инфраструктуры       |

##  Требования

- **Java 25** или выше
- **Maven 3.9+**
- **Docker** и **Docker Compose**
- **Git**

##  Быстрый старт

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

## База данных

### Схема данных

```sql
-- Основные таблицы
user          -- Информация о пользователях
├── id (PK)
├── email (UNIQUE)
├── phone_number (UNIQUE)
├── password 
├── firstName
├── lastName
├── role (ENUM)
└── created_at

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

## Карта web-сайта

### Публичные страницы

| Метод  | Endpoint    | Описание                    |
|--------|-------------|-----------------------------|
| `GET`  | `/`         | Редирект на '/home'         |
| `GET`  | `/home`     | Главная страница            |
| `GET`  | `/register` | Страница регистрации        |
| `POST` | `/register`    | Обработка формы регистрации |
| `GET`  | `/login`    | Страница авторизации        |
| `POST`  | `/login`    | Обработка формы авторизации |

### Страницы для авторизированных пользователей

| Метод | Endpoint | Описание |
|-------|----------|--|


## 📁 Структура проекта

```
src/main/java/com/bank/account/
├── BankAccountServiceApplication.java # Главный файл
├── controller/         # Контроллеры
│   ├── AccountController.java
│   ├── AuthController.java
│   └── HomeController.java
├── service/           # Бизнес-логика
│   ├── AccountService.java
│   ├── CustomUserDetailsService.java
│   └── UserService.java
├── repository/        # Доступ к данным
│   ├── AccountRepository.java
│   ├── TransactionRepository.java
│   └── UserRepository.java 
├── model/             # Сущности JPA
│   ├── Account.java
│   ├── Role.java
│   ├── Transaction.java
│   └── User.java
├── dto/               # Data Transfer Objects
│   ├── AccountDTO.java
│   ├── CreateAccountRequest.java
│   ├── ErrorResponse.java
│   ├── RegistrationRequest.java
│   ├── TransactionDTO.java
│   ├── TransactionRequest.java
│   ├── TransferRequest.java
│   └── TransferResultDTO.java
└── security/            # Безопасность
    ├── SecurityConfig.java

src/main/resources/
├── static/ 
│   ├── css/
│   │   ├── home.css
│   │   ├── login.css
│   │   ├── register.css
│   │   └── style.css
│   ├── js/
│   │   └── index.js
├── templates/
│   ├── home.html
│   ├── login.html
│   └── register.html
├── application.yml          # Основная конфигурация
```

## 📞 Контакты

Матвей Андреевич - [@waterflod](https://t.me/waterflod) - mse25019@gmail.com

Ссылка на проект: [https://github.com/WaterFlod/BankAccountManagmentService](https://github.com/WaterFlod/BankAccountManagmentService)

---

<div align="center">

### ⭐ Если проект вам понравился, поставьте звезду на GitHub!

</div>