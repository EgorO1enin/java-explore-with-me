package ru.practicum.ewm.stats.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.ewm.stats.client.StatsClient;

/**
 * Конфигурация клиента статистики
 */
@Configuration
public class StatsClientConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "stats.client")
    public StatsClientProperties statsClientProperties() {
        return new StatsClientProperties();
    }

    @Bean
    public WebClient statsWebClient(StatsClientProperties properties) {
        return WebClient.builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    @Bean
    public StatsClient statsClient(WebClient statsWebClient) {
        return new StatsClient(statsWebClient);
    }
}
