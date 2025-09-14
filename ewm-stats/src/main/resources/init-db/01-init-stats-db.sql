-- Создание таблицы для статистики
CREATE TABLE IF NOT EXISTS hits (
    id BIGSERIAL PRIMARY KEY,
    app VARCHAR(255) NOT NULL,
    uri VARCHAR(255) NOT NULL,
    ip VARCHAR(45) NOT NULL,
    timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

-- Создание индексов для оптимизации запросов
CREATE INDEX IF NOT EXISTS idx_hits_app ON hits(app);
CREATE INDEX IF NOT EXISTS idx_hits_uri ON hits(uri);
CREATE INDEX IF NOT EXISTS idx_hits_timestamp ON hits(timestamp);
CREATE INDEX IF NOT EXISTS idx_hits_app_uri ON hits(app, uri);
