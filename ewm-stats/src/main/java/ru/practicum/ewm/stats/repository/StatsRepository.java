package ru.practicum.ewm.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.stats.model.EndpointHitEntityModel;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHitEntityModel, Long> {

    @Query("SELECT new ru.practicum.ewm.stats.dto.ViewStats(e.app, e.uri, COUNT(e)) " +
           "FROM EndpointHitEntityModel e " +
           "WHERE e.timestamp BETWEEN :start AND :end " +
           "AND (:uris IS NULL OR e.uri IN :uris) " +
           "GROUP BY e.app, e.uri " +
           "ORDER BY COUNT(e) DESC")
    List<ru.practicum.ewm.stats.dto.ViewStats> getStats(@Param("start") LocalDateTime start,
                                                         @Param("end") LocalDateTime end,
                                                         @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.ewm.stats.dto.ViewStats(e.app, e.uri, COUNT(DISTINCT e.ip)) " +
           "FROM EndpointHitEntityModel e " +
           "WHERE e.timestamp BETWEEN :start AND :end " +
           "AND (:uris IS NULL OR e.uri IN :uris) " +
           "GROUP BY e.app, e.uri " +
           "ORDER BY COUNT(DISTINCT e.ip) DESC")
    List<ru.practicum.ewm.stats.dto.ViewStats> getUniqueStats(@Param("start") LocalDateTime start,
                                                              @Param("end") LocalDateTime end,
                                                              @Param("uris") List<String> uris);
}
