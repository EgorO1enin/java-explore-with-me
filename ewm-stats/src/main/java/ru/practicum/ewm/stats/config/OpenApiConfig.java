package ru.practicum.ewm.stats.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Конфигурация OpenAPI (Swagger) для сервиса статистики
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Explore With Me Stats Service API")
                        .description("API сервиса статистики для приложения \"Explore With Me\".\n\n" +
                                "Сервис предназначен для сбора и анализа статистики посещений эндпоинтов основного сервиса.")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Explore With Me Team")
                                .email("team@explorewithme.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:9090")
                                .description("Development server"),
                        new Server()
                                .url("http://stats-service:9090")
                                .description("Docker environment")
                ));
    }
}
