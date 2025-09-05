package ru.practicum.ewm.stats.service;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;
import ru.practicum.ewm.stats.model.EndpointHitEntityModel;
import ru.practicum.ewm.stats.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatsService {
    private final StatsRepository statsRepository;

    public StatsService(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    public void saveHit(EndpointHit endpointHit) {
        EndpointHitEntityModel entity = new EndpointHitEntityModel();
        entity.setApp(endpointHit.getApp());
        entity.setUri(endpointHit.getUri());
        entity.setIp(endpointHit.getIp());
        entity.setTimestamp(endpointHit.getTimestamp());

        statsRepository.save(entity);
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (Boolean.TRUE.equals(unique)) {
            return statsRepository.getUniqueStats(start, end, uris);
        } else {
            return statsRepository.getStats(start, end, uris);
        }
    }
}
