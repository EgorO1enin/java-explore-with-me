# 🐳 Docker Setup для сервиса статистики

## 🚀 Быстрый запуск

### 1. Запуск всех сервисов
```bash
./start-docker.sh
```

### 2. Остановка сервисов
```bash
./stop-docker.sh
```

### 3. Ручной запуск
```bash
docker-compose up -d
```

## 📊 Что запускается

### 🗄️ База данных PostgreSQL
- **Порт**: 5432
- **База данных**: ewm_stats
- **Пользователь**: postgres
- **Пароль**: postgres
- **Health Check**: Автоматическая проверка готовности

### 🖥️ Сервис статистики
- **Порт**: 9090
- **Swagger UI**: http://localhost:9090/swagger-ui.html
- **API Docs**: http://localhost:9090/api-docs
- **Health Check**: http://localhost:9090/actuator/health

## 🔧 Архитектура Docker

```
┌─────────────────────────────────────────────────────────┐
│                    Docker Network                       │
│                                                         │
│  ┌─────────────────┐    ┌─────────────────┐            │
│  │  stats-server   │    │   stats-db      │            │
│  │  (ewm-stats)    │◄──►│  (PostgreSQL)   │            │
│  │  Port: 9090     │    │  Port: 5432     │            │
│  └─────────────────┘    └─────────────────┘            │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

## 📝 Полезные команды

### Просмотр логов
```bash
# Логи сервиса статистики
docker-compose logs -f stats-server

# Логи базы данных
docker-compose logs -f stats-db

# Все логи
docker-compose logs -f
```

### Управление контейнерами
```bash
# Перезапуск сервиса
docker-compose restart stats-server

# Остановка всех сервисов
docker-compose down

# Остановка с удалением данных
docker-compose down -v

# Пересборка образов
docker-compose build --no-cache
```

### Подключение к базе данных
```bash
# Через Docker
docker exec -it ewm-stats-db psql -U postgres -d ewm_stats

# Через внешний клиент
# Host: localhost
# Port: 5432
# Database: ewm_stats
# Username: postgres
# Password: postgres
```

## 🧪 Тестирование API

### 1. Сохранение статистики
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

### 2. Получение статистики
```bash
curl "http://localhost:9090/stats?start=2024-01-15%2000:00:00&end=2024-01-15%2023:59:59&unique=false"
```

### 3. Health Check
```bash
curl http://localhost:9090/actuator/health
```

## 🔍 Мониторинг

### Статус сервисов
```bash
docker-compose ps
```

### Использование ресурсов
```bash
docker stats
```

### Проверка здоровья
```bash
# База данных
docker exec ewm-stats-db pg_isready -U postgres -d ewm_stats

# Сервис статистики
curl -f http://localhost:9090/actuator/health
```

## 🛠️ Разработка

### Локальная разработка
```bash
# Запуск только базы данных
docker-compose up -d stats-db

# Запуск сервиса локально
cd ewm-stats
mvn spring-boot:run
```

### Отладка
```bash
# Подключение к контейнеру сервиса
docker exec -it ewm-server bash

# Просмотр переменных окружения
docker exec ewm-server env
```

## 📊 Структура данных

### Таблица endpoint_hits
```sql
CREATE TABLE endpoint_hits (
    id BIGSERIAL PRIMARY KEY,
    app VARCHAR(255) NOT NULL,
    uri VARCHAR(255) NOT NULL,
    ip VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP NOT NULL
);
```

### Индексы для оптимизации
- `idx_endpoint_hits_timestamp` - по времени
- `idx_endpoint_hits_uri` - по URI
- `idx_endpoint_hits_app` - по приложению
- `idx_endpoint_hits_timestamp_uri` - составной индекс
- `idx_endpoint_hits_timestamp_uri_ip` - для уникальной статистики

## 🚨 Troubleshooting

### Проблема: Сервис не запускается
```bash
# Проверьте логи
docker-compose logs stats-server

# Проверьте доступность базы данных
docker-compose logs stats-db
```

### Проблема: База данных недоступна
```bash
# Перезапустите базу данных
docker-compose restart stats-db

# Проверьте статус
docker-compose ps
```

### Проблема: Порт занят
```bash
# Найдите процесс, использующий порт
lsof -i :9090
lsof -i :5432

# Остановите конфликтующий процесс
```

## 🔐 Безопасность

- Сервис запускается под непривилегированным пользователем
- Health checks настроены для мониторинга
- Автоматический перезапуск при сбоях
- Изолированная Docker сеть
