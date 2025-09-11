-- Инициализация базы данных для сервиса статистики
-- Этот скрипт выполняется автоматически при первом запуске PostgreSQL
-- Создание таблицы для хранения статистики
CREATE TABLE IF NOT EXISTS endpoint_hits (
    id BIGSERIAL PRIMARY KEY,
    app VARCHAR(255) NOT NULL,
    uri VARCHAR(255) NOT NULL,
    ip VARCHAR(45) NOT NULL,
    timestamp TIMESTAMP NOT NULL
);