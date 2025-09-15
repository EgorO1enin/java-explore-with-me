package ru.practicum.ewm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ru.practicum.ewm.stats.client.config.StatsClientProperties;

@Configuration
public class StatsClientConfig {

    @Value("${stats-service.url:http://localhost:9090}")
    private String statsServiceUrl;

    @Bean
    @Primary
    public StatsClientProperties statsClientProperties() {
        StatsClientProperties properties = new StatsClientProperties();
        properties.setUrl(statsServiceUrl);
        return properties;
    }
}
