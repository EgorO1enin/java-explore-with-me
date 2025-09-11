# Многоэтапная сборка для оптимизации размера образа
FROM openjdk:21-jdk-slim AS builder

# Установка Maven
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Установка рабочей директории
WORKDIR /app

# Копирование файлов проекта
COPY pom.xml .
COPY ewm-stats-dto/pom.xml ewm-stats-dto/
COPY ewm-stats-client/pom.xml ewm-stats-client/
COPY ewm-stats/pom.xml ewm-stats/

# Копирование исходного кода
COPY ewm-stats-dto/src ewm-stats-dto/src
COPY ewm-stats-client/src ewm-stats-client/src
COPY ewm-stats/src ewm-stats/src

# Сборка проекта
RUN mvn clean package -DskipTests

# Финальный образ
FROM openjdk:21-jdk-slim

# Установка curl для health checks
RUN apt-get update && \
    apt-get install -y curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Копирование собранного JAR файла
COPY --from=builder /app/ewm-stats/target/ewm-stats-*.jar app.jar

# Создание пользователя для безопасности
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

EXPOSE 9090

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:9090/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
