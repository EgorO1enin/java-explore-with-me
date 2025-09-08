package ru.practicum.ewm.stats.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import lombok.NoArgsConstructor;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Конфигурация OpenAPI (Swagger) для сервиса статистики
 */
@NoArgsConstructor
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
                                .name("Egor Olenin")
                                .email("iegorreus@yandex.ru")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:9090")
                                .description("Development server"),
                        new Server()
                                .url("http://stats-service:9090")
                                .description("Docker environment")
                ));
    }

    @Bean
    public SwaggerUiConfigParameters swaggerUiConfigParameters(SwaggerUiConfigProperties swaggerUiConfigProperties) {
        SwaggerUiConfigParameters parameters = new SwaggerUiConfigParameters(swaggerUiConfigProperties);
        parameters.setPath("/swagger-ui.html");
        parameters.setTryItOutEnabled(true);
        parameters.setOperationsSorter("method");
        parameters.setTagsSorter("alpha");
        parameters.setFilter(String.valueOf(true));
        return parameters;
    }
}