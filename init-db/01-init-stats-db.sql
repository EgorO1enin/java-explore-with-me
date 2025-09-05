-- Инициализация базы данных для сервиса статистики
-- Этот скрипт выполняется автоматически при первом запуске PostgreSQL

-- Создание таблицы для хранения статистики (если не существует)
-- Spring Boot JPA создаст таблицу автоматически, но можно добавить индексы

-- Создание индексов для оптимизации запросов
-- (Выполнится после создания таблицы Spring Boot)

-- Индекс по времени для быстрого поиска по диапазону дат
CREATE INDEX IF NOT EXISTS idx_endpoint_hits_timestamp ON endpoint_hits(timestamp);

-- Индекс по URI для быстрого поиска по конкретному эндпоинту
CREATE INDEX IF NOT EXISTS idx_endpoint_hits_uri ON endpoint_hits(uri);

-- Индекс по приложению для фильтрации по сервису
CREATE INDEX IF NOT EXISTS idx_endpoint_hits_app ON endpoint_hits(app);

-- Составной индекс для оптимизации основных запросов
CREATE INDEX IF NOT EXISTS idx_endpoint_hits_timestamp_uri ON endpoint_hits(timestamp, uri);

-- Составной индекс для уникальной статистики
CREATE INDEX IF NOT EXISTS idx_endpoint_hits_timestamp_uri_ip ON endpoint_hits(timestamp, uri, ip);

-- Настройка статистики таблиц для оптимизатора запросов
ANALYZE endpoint_hits;
