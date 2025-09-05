# Explore With Me - Сервис статистики

## Описание проекта

ExploreWithMe — это платформа-афиша для организации и поиска интересных мероприятий. Проект состоит из двух сервисов:
- **Основной сервис** (ewm-main-service) - содержит всю бизнес-логику приложения
- **Сервис статистики** (ewm-stats) - собирает аналитику по просмотрам и активности

## Структура проекта

```
explore-with-me/
├── ewm-stats-dto/          # Общие DTO классы с аннотациями Swagger
├── ewm-stats-client/       # HTTP клиент для сервиса статистики (WebClient + Feign)
├── ewm-stats/              # Основной сервис статистики с Swagger UI
├── docker-compose.yml      # Конфигурация Docker с health checks
└── pom.xml                 # Родительский POM с управлением зависимостями
```

## Требования

- Java 21
- Maven 3.6+
- PostgreSQL 16.1 (для локального запуска)

## Локальный запуск

### 1. Подготовка базы данных

Создайте базу данных PostgreSQL:
```sql
CREATE DATABASE ewm_stats;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE ewm_stats TO postgres;
```

### 2. Сборка проекта

```bash
# Сборка всех модулей
mvn clean install

# Или сборка по модулям
mvn clean install -pl ewm-stats-dto
mvn clean install -pl ewm-stats-client
mvn clean install -pl ewm-stats
```

### 3. Запуск сервиса статистики

```bash
# Переход в модуль сервиса
cd ewm-stats

# Запуск приложения
mvn spring-boot:run
```

Сервис будет доступен по адресу: http://localhost:9090

### 4. Проверка работы

#### Health check
```bash
curl http://localhost:9090/actuator/health
```

#### Сохранение статистики
```bash
curl -X POST http://localhost:9090/hit \
  -H "Content-Type: application/json" \
  -d '{
    "app": "ewm-main-service",
    "uri": "/events/1",
    "ip": "192.168.1.100",
    "timestamp": "2024-01-15 14:30:00"
  }'
```

#### Получение статистики
```bash
curl "http://localhost:9090/stats?start=2024-01-15%2000:00:00&end=2024-01-15%2023:59:59&unique=false"
```

## API Endpoints

### POST /hit
Сохраняет информацию о запросе к эндпоинту.

**Request Body:**
```json
{
  "app": "ewm-main-service",
  "uri": "/events/1",
  "ip": "192.168.1.100",
  "timestamp": "2024-01-15 14:30:00"
}
```

### GET /stats
Получает статистику по посещениям.

**Query Parameters:**
- `start` (required) - дата и время начала диапазона (yyyy-MM-dd HH:mm:ss)
- `end` (required) - дата и время конца диапазона (yyyy-MM-dd HH:mm:ss)
- `uris` (optional) - список URI для фильтрации
- `unique` (optional) - учитывать только уникальные посещения (по IP)

## Конфигурация

Основные настройки находятся в `ewm-stats/src/main/resources/application.yml`:

- **Порт:** 9090
- **База данных:** PostgreSQL на localhost:5432
- **База:** ewm_stats
- **Пользователь:** postgres
- **Пароль:** postgres

## Docker запуск

### 🚀 Быстрый запуск
```bash
# Запуск всех сервисов одной командой
./start-docker.sh

# Остановка сервисов
./stop-docker.sh
```

### 🔧 Ручной запуск
```bash
# Сборка и запуск всех сервисов
docker-compose up -d

# Только база данных
docker-compose up -d stats-db

# Сборка и запуск сервиса
docker-compose up --build stats-server
```

### 📊 Доступные сервисы после запуска:
- **Сервис статистики**: http://localhost:9090
- **Swagger UI**: http://localhost:9090/swagger-ui.html
- **API Docs**: http://localhost:9090/api-docs
- **Health Check**: http://localhost:9090/actuator/health
- **PostgreSQL**: localhost:5432 (ewm_stats/postgres/postgres)

### 📝 Полезные команды:
```bash
# Просмотр логов
docker-compose logs -f stats-server

# Статус сервисов
docker-compose ps

# Перезапуск сервиса
docker-compose restart stats-server
```

Подробная документация по Docker: [DOCKER.md](DOCKER.md)

## Разработка

### Добавление новых эндпоинтов

1. Создайте DTO в модуле `ewm-stats-dto`
2. Добавьте модель в `ewm-stats/src/main/java/ru/practicum/ewm/stats/model/`
3. Создайте репозиторий в `ewm-stats/src/main/java/ru/practicum/ewm/stats/repository/`
4. Добавьте сервис в `ewm-stats/src/main/java/ru/practicum/ewm/stats/service/`
5. Создайте контроллер в `ewm-stats/src/main/java/ru/practicum/ewm/stats/controller/`

### Тестирование

```bash
# Запуск тестов
mvn test

# Запуск тестов с покрытием
mvn test jacoco:report
```

## Swagger UI

После запуска приложения Swagger UI будет доступен по адресу:
- http://localhost:9090/swagger-ui.html - интерфейс Swagger UI
- http://localhost:9090/api-docs - OpenAPI спецификация в JSON формате

### Возможности Swagger UI:
- Интерактивное тестирование API
- Просмотр документации по всем эндпоинтам
- Валидация запросов
- Примеры запросов и ответов

## Клиент для сервиса статистики

Проект включает HTTP клиент для взаимодействия с сервисом статистики:

### WebClient (рекомендуемый)
```java
@Autowired
private StatsClient statsClient;

// Сохранить статистику
statsClient.saveHit("ewm-main-service", "/events/1", "192.168.1.100", LocalDateTime.now());

// Получить статистику
List<ViewStats> stats = statsClient.getStats(start, end, uris, false);
```

### Feign Client (альтернативный)
```java
@Autowired
private StatsFeignClient statsFeignClient;

// Использование аналогично WebClient
EndpointHit hit = new EndpointHit(null, "ewm-main-service", "/events/1", "192.168.1.100", LocalDateTime.now());
statsFeignClient.saveHit(hit);
```

### Конфигурация клиента
```yaml
stats:
  client:
    url: http://localhost:9090
    connect-timeout: 5000
    read-timeout: 10000
```

## Архитектурные улучшения

### Модульная структура
- **ewm-stats-dto**: Переиспользуемые DTO с валидацией и аннотациями Swagger
- **ewm-stats-client**: Универсальный клиент с поддержкой WebClient и Feign
- **ewm-stats**: Сервис с полной OpenAPI документацией

### Технологический стек
- **Spring Boot 3.3.2** - основной фреймворк
- **Java 21** - стабильная LTS версия Java
- **SpringDoc OpenAPI 3** - генерация документации API
- **Spring WebFlux** - для реактивного HTTP клиента
- **Spring Cloud OpenFeign** - декларативный HTTP клиент
- **PostgreSQL 16.1** - база данных
- **Docker** - контейнеризация

### Особенности реализации
- Автоконфигурация клиента через Spring Boot
- Поддержка различных типов HTTP клиентов
- Полная документация API через Swagger UI
- Health checks для мониторинга
- Утилиты для работы с HTTP запросами

## Логи

Логи приложения выводятся в консоль. Для настройки уровня логирования добавьте в `application.yml`:

```yaml
logging:
  level:
    ru.practicum.ewm.stats: DEBUG
    org.springframework.web: DEBUG
```
