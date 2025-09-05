package ru.practicum.ewm.stats.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.ewm.stats.dto.EndpointHit;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Базовые тесты для StatsClient
 */
public class StatsClientTest {

    private StatsClient statsClient;

    @BeforeEach
    void setUp() {
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:9090")
                .build();
        statsClient = new StatsClient(webClient);
    }

    @Test
    void shouldCreateStatsClient() {
        assertNotNull(statsClient);
    }

    @Test
    void shouldCreateEndpointHit() {
        EndpointHit hit = new EndpointHit(
                null,
                "test-app",
                "/test/uri",
                "127.0.0.1",
                LocalDateTime.now()
        );

        assertNotNull(hit);
        assertNotNull(hit.getApp());
        assertNotNull(hit.getUri());
        assertNotNull(hit.getIp());
        assertNotNull(hit.getTimestamp());
    }
}
