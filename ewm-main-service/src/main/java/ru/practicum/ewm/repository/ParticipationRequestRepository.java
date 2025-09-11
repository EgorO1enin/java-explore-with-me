package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.model.enums.RequestStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    
    List<ParticipationRequest> findByRequesterId(Long requesterId);
    
    List<ParticipationRequest> findByEventId(Long eventId);
    
    Optional<ParticipationRequest> findByEventIdAndRequesterId(Long eventId, Long requesterId);
    
    @Query("SELECT pr FROM ParticipationRequest pr WHERE pr.event.id = :eventId AND pr.status = :status")
    List<ParticipationRequest> findByEventIdAndStatus(@Param("eventId") Long eventId, @Param("status") RequestStatus status);
    
    @Query("SELECT COUNT(pr) FROM ParticipationRequest pr WHERE pr.event.id = :eventId AND pr.status = 'CONFIRMED'")
    long countConfirmedRequestsByEventId(@Param("eventId") Long eventId);
}
