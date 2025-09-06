package ru.practicum.ewm.stats.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Настройки клиента статистики
 */
@Data
@ConfigurationProperties(prefix = "stats.client")
public class StatsClientProperties {

    /**
     * URL сервиса статистики
     */
    private String url = "http://localhost:9090";

    /**
     * Timeout для подключения в миллисекундах
     */
    private Integer connectTimeout = 5000;

    /**
     * Timeout для чтения в миллисекундах
     */
    private Integer readTimeout = 10000;
}
