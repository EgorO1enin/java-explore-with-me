package ru.practicum.ewm.stats.client;

import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * HTTP клиент для взаимодействия с сервисом статистики
 */
public class StatsClient {
    private final WebClient webClient;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsClient(String statsServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(statsServiceUrl)
                .build();
    }

    public StatsClient(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Сохранить информацию о запросе к эндпоинту
     *
     * @param endpointHit информация о запросе
     */
    public void saveHit(EndpointHit endpointHit) {
        webClient.post()
                .uri("/hit")
                .bodyValue(endpointHit)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    /**
     * Сохранить информацию о запросе к эндпоинту
     *
     * @param app идентификатор сервиса
     * @param uri URI для которого записывается информация
     * @param ip IP-адрес пользователя
     * @param timestamp время запроса
     */
    public void saveHit(String app, String uri, String ip, LocalDateTime timestamp) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp(app);
        endpointHit.setUri(uri);
        endpointHit.setIp(ip);
        endpointHit.setTimestamp(timestamp);
        saveHit(endpointHit);
    }

    /**
     * Получить статистику посещений
     *
     * @param start дата и время начала диапазона
     * @param end дата и время конца диапазона
     * @param uris список URI для которых нужна статистика (может быть null)
     * @param unique учитывать только уникальные посещения (по IP)
     * @return список статистики
     */
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        String startFormatted = start.format(DATE_FORMAT);
        String endFormatted = end.format(DATE_FORMAT);

        WebClient.RequestHeadersUriSpec<?> requestSpec = (WebClient.RequestHeadersUriSpec<?>) webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/stats")
                            .queryParam("start", startFormatted)
                            .queryParam("end", endFormatted)
                            .queryParam("unique", unique);

                    if (uris != null && !uris.isEmpty()) {
                        uriBuilder.queryParam("uris", String.join(",", uris));
                    }

                    return uriBuilder.build();
                });

        return requestSpec.retrieve()
                .bodyToFlux(ViewStats.class)
                .collectList()
                .block();
    }

    /**
     * Получить статистику посещений (только уникальные)
     */
    public List<ViewStats> getUniqueStats(LocalDateTime start, LocalDateTime end, List<String> uris) {
        return getStats(start, end, uris, true);
    }

    /**
     * Получить статистику посещений (все)
     */
    public List<ViewStats> getAllStats(LocalDateTime start, LocalDateTime end, List<String> uris) {
        return getStats(start, end, uris, false);
    }
}
