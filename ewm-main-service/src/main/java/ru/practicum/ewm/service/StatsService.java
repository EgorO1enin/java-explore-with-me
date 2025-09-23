package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.client.StatsClient;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsService {

    private final StatsClient statsClient;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void saveHit(String app, String uri, String ip, LocalDateTime timestamp) {

        try {
            EndpointHit endpointHit = EndpointHit.builder()
                    .app(app)
                    .uri(uri)
                    .ip(ip)
                    .timestamp(timestamp)
                    .build();

            statsClient.saveHit(app, uri, ip, timestamp);
        } catch (Exception e) {
            log.error("❌ Ошибка при сохранении статистики: {}", e.getMessage(), e);
        }
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {

        try {
            return statsClient.getStats(start, end, uris, unique);
        } catch (Exception e) {
            log.error("Ошибка при получении статистики: {}", e.getMessage());
            return List.of();
        }
    }

    public Long getEventViews(Long eventId) {

        LocalDateTime start = LocalDateTime.now().minusYears(1);
        LocalDateTime end = LocalDateTime.now();
        List<String> uris = List.of("/events/" + eventId);

        List<ViewStats> stats = getStats(start, end, uris, true);

        if (stats.isEmpty()) {
            return 0L;
        }

        return stats.get(0).getHits();
    }
}