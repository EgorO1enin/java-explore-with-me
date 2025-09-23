# Explore With Me — Основной сервис и сервис статистики

## Описание проекта

ExploreWithMe — платформа-афиша для организации и поиска мероприятий. Проект состоит из двух сервисов:
- **Основной сервис** (`ewm-main-service`) — бизнес-логика, события, пользователи, комментарии
- **Сервис статистики** (`ewm-stats`) — сбор и предоставление аналитики по просмотрам

## Структура проекта

```
explore-with-me/
├── ewm-main-service/       # Основной сервис (REST API, комментарии, события)
├── ewm-stats/              # Сервис статистики с Swagger UI
├── ewm-stats-client/       # HTTP-клиент для сервиса статистики
├── ewm-stats-dto/          # Общие DTO для сервиса статистики
├── init-db/                # SQL-скрипты инициализации БД (stats и main)
├── docker-compose.yml      # Композиция обоих сервисов и БД
└── pom.xml                 # Родительский POM
```

## Требования

- Java 21
- Maven 3.6+
- Docker (для упрощённого запуска)

## Быстрый старт в Docker

```bash
# Сборка образов и запуск всех сервисов
docker-compose up -d --build

# Просмотр статуса
docker-compose ps
```

Доступные сервисы:
- Основной сервис: http://localhost:8080
- Сервис статистики: http://localhost:9090
- Swagger UI (stats): http://localhost:9090/swagger-ui.html
- API Docs (stats): http://localhost:9090/api-docs
- PostgreSQL (main): localhost:5433, БД `ewm_main` (postgres/postgres)
- PostgreSQL (stats): localhost:5432, БД `ewm_stats` (postgres/postgres)

## Локальный запуск без Docker

### 1) Базы данных

Поднимите две БД PostgreSQL или используйте `docker-compose` только для БД:

```bash
docker-compose up -d stats-db main-db
```

### 2) Сервис статистики

```bash
cd ewm-stats
mvn spring-boot:run
```

### 3) Основной сервис

```bash
cd ewm-main-service
mvn spring-boot:run
```

Профили и параметры подключений прописаны в `application.yml`/`application-docker.yml` каждого сервиса.

## Функциональность комментариев (ewm-main-service)

Реализована система комментариев к событиям с модерацией.

Модели:
- `Comment` (связи с `User` и `Event`)
- `CommentStatus` = `PENDING | APPROVED | REJECTED`

Основные DTO:
- `NewCommentDto { text }`
- `CommentDto { id, text, author(UserShortDto), event(Long), created, updated, status }`
- `UpdateCommentRequest { text }`
- `UpdateCommentAdminRequest { status }` где `status` ∈ {`APPROVED`,`REJECTED`}

Эндпоинты:
- Публичные:
  - `GET /events/{eventId}/comments` — одобренные комментарии события (пагинация `from,size`)
  - `GET /events/comments/search?text=...` — поиск одобренных комментариев по тексту
- Приватные (для пользователя `userId`):
  - `POST /users/{userId}/events/{eventId}/comments` — создать комментарий (статус `PENDING`)
  - `GET /users/{userId}/comments` — список своих комментариев
  - `PATCH /users/{userId}/comments/{commentId}` — обновить комментарий в статусе `PENDING`
  - `DELETE /users/{userId}/comments/{commentId}` — удалить комментарий в статусе `PENDING`
- Административные:
  - `GET /admin/comments` — список комментариев в статусе `PENDING`
  - `PATCH /admin/comments/{commentId}` — модерация (`APPROVED`/`REJECTED`)
  - `DELETE /admin/comments/{commentId}` — удалить любой комментарий

Примеры запросов:

```bash
# Создание комментария
curl -X POST "http://localhost:8080/users/1/events/10/comments" \
  -H "Content-Type: application/json" \
  -d '{"text":"Отличное событие!"}'

# Получение одобренных комментариев события
curl "http://localhost:8080/events/10/comments?from=0&size=10"

# Модерация админом
curl -X PATCH "http://localhost:8080/admin/comments/5" \
  -H "Content-Type: application/json" \
  -d '{"status":"APPROVED"}'
```

Ограничения и валидация:
- Комментировать можно только опубликованные события
- Длина текста 1..2000 символов
- Редактирование/удаление пользователем — только в статусе `PENDING`
- Публично видны только `APPROVED`

Интеграция со статистикой: основные публичные/приватные админ-эндпоинты сохраняют хиты через `StatsService`.

## Сервис статистики (ewm-stats)

Основные эндпоинты:
- `POST /hit` — сохранить обращение
- `GET /stats` — получить статистику за период (`start`,`end`,`uris`,`unique`)

Пример:
```bash
curl -X POST http://localhost:9090/hit -H "Content-Type: application/json" -d '{
  "app":"ewm-main-service", "uri":"/events/10", "ip":"127.0.0.1", "timestamp":"2024-01-15 14:30:00"
}'
```

## Разработка

### Сборка

```bash
mvn clean install
```

### Тесты

```bash
mvn test
```

### Checkstyle

```bash
mvn -DskipTests checkstyle:check
```

## Docker

Подробности и полезные команды — в файле [DOCKER.md](DOCKER.md).
