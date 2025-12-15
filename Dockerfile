FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

# Копируем pom.xml для кэширования зависимостей
COPY pom.xml .

# Скачиваем зависимости (кешируются в слое Docker)
RUN mvn dependency:go-offline -B

# Копируем исходный код
COPY src ./src

# Собираем приложение
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080

# Добавляем non-root пользователя для безопасности
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ENTRYPOINT ["java", "-jar", "app.jar"]