package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    Page<Event> findByInitiatorId(Long initiatorId, Pageable pageable);
    
    Optional<Event> findByIdAndInitiatorId(Long eventId, Long initiatorId);
    
    // Удаляем проблемные методы с @Query
    
    @Query("SELECT e FROM Event e WHERE e.state = 'PUBLISHED' AND " +
           "(:text IS NULL OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) AND " +
           "(:categories IS NULL OR e.category.id IN (:categories)) AND " +
           "(:paid IS NULL OR e.paid = :paid) AND " +
           "(:rangeStart IS NULL OR e.eventDate >= :rangeStart) AND " +
           "(:rangeEnd IS NULL OR e.eventDate <= :rangeEnd) AND " +
           "(:onlyAvailable IS NULL OR :onlyAvailable = false OR e.participantLimit = 0 OR e.confirmedRequests < e.participantLimit)")
    Page<Event> findPublishedEventsByFilters(
            @Param("text") String text,
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("onlyAvailable") Boolean onlyAvailable,
            Pageable pageable);
    
    @Query("SELECT e FROM Event e WHERE e.id IN :eventIds")
    List<Event> findByIdIn(@Param("eventIds") List<Long> eventIds);
    
    @Query("SELECT COUNT(pr) FROM ParticipationRequest pr WHERE pr.event.id = :eventId AND pr.status = 'CONFIRMED'")
    long countConfirmedRequestsByEventId(@Param("eventId") Long eventId);
    
    long countByCategoryId(Long categoryId);
}
