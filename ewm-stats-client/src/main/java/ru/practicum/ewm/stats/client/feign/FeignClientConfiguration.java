package ru.practicum.ewm.stats.client.feign;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация для включения Feign клиентов
 */
@Configuration
@EnableFeignClients(basePackages = "ru.practicum.ewm.stats.client.feign")
public class FeignClientConfiguration {
}
