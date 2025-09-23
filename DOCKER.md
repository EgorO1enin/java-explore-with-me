# 🐳 Docker Setup (Main + Stats)

## 🚀 Быстрый запуск

```bash
docker-compose up -d --build
```

Проверка статуса:
```bash
docker-compose ps
```

## 📊 Что запускается

### 🗄️ PostgreSQL (stats)
- Порт: 5432 → БД `ewm_stats` (postgres/postgres)

### 🗄️ PostgreSQL (main)
- Порт: 5433 → БД `ewm_main` (postgres/postgres)

### 🖥️ Сервис статистики (ewm-stats)
- Порт: 9090
- Swagger UI: http://localhost:9090/swagger-ui.html
- API Docs: http://localhost:9090/api-docs
- Health: http://localhost:9090/actuator/health

### 🖥️ Основной сервис (ewm-main-service)
- Порт: 8080
- Профиль: `docker`

## 🔧 Архитектура Docker

```
┌─────────────────────────────────────────────────────────┐
│                    Docker Network                       │
│                                                         │
│  ┌─────────────────┐    ┌─────────────────┐            │
│  │  stats-server   │    │   stats-db      │            │
│  │  Port: 9090     │◄──►│  Port: 5432     │            │
│  └─────────────────┘    └─────────────────┘            │
│  ┌─────────────────┐    ┌─────────────────┐            │
│  │  main-service   │    │   main-db       │            │
│  │  Port: 8080     │◄──►│  Port: 5433     │            │
│  └─────────────────┘    └─────────────────┘            │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

## 📝 Полезные команды

### Логи
```bash
docker-compose logs -f stats-server
docker-compose logs -f ewm-main-service
```

### Пересборка и рестарт
```bash
docker-compose build --no-cache
docker-compose restart stats-server ewm-main-service
```

### Остановка
```bash
docker-compose down  # без удаления данных
docker-compose down -v  # с удалением volumes
```

## 🧪 Быстрые проверки

### Статистика
```bash
curl -X POST http://localhost:9090/hit -H "Content-Type: application/json" -d '{
  "app": "ewm-main-service", "uri": "/events/1", "ip": "127.0.0.1", "timestamp": "2024-01-15 14:30:00"
}'

curl "http://localhost:9090/stats?start=2024-01-15%2000:00:00&end=2024-01-15%2023:59:59&unique=false"
```

### Комментарии (основной сервис)
```bash
# Создать комментарий
curl -X POST "http://localhost:8080/users/1/events/10/comments" -H "Content-Type: application/json" -d '{"text":"Отлично!"}'

# Получить одобренные комментарии события
curl "http://localhost:8080/events/10/comments?from=0&size=5"
```

## 🔐 Безопасность и здоровье
- Health checks настроены для БД и сервиса статистики
- Сервисы в изолированной сети `ewm-network`
