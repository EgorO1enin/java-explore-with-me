package ru.practicum.ewm.stats.client.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Конфигурация клиента статистики
 */
@Configuration
@EnableConfigurationProperties(StatsClientProperties.class)
public class StatsClientConfiguration {

    @Bean
    public WebClient statsWebClient(StatsClientProperties properties) {
        return WebClient.builder()
                .baseUrl(properties.getUrl())
                .build();
    }

}
