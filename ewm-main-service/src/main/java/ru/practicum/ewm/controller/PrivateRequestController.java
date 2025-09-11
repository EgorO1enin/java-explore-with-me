package ru.practicum.ewm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.response.ParticipationRequestDto;
import ru.practicum.ewm.service.ParticipationRequestService;
import ru.practicum.ewm.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Private: Запросы", description = "API для работы с запросами на участие в событиях")
public class PrivateRequestController {
    
    private final ParticipationRequestService participationRequestService;
    private final StatsService statsService;
    
    @GetMapping
    @Operation(summary = "Получение информации о заявках текущего пользователя на участие в чужих событиях")
    public ResponseEntity<List<ParticipationRequestDto>> getUserRequests(
            @Parameter(description = "ID пользователя") @PathVariable Long userId,
            HttpServletRequest request) {
        log.info("GET /users/{}/requests - получение запросов пользователя", userId);
        statsService.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
        
        List<ParticipationRequestDto> requests = participationRequestService.getUserRequests(userId);
        return ResponseEntity.ok(requests);
    }
    
    @PostMapping
    @Operation(summary = "Добавление запроса от текущего пользователя на участие в событии")
    public ResponseEntity<ParticipationRequestDto> createRequest(
            @Parameter(description = "ID пользователя") @PathVariable Long userId,
            @Parameter(description = "ID события") @RequestParam Long eventId,
            HttpServletRequest request) {
        log.info("POST /users/{}/requests?eventId={} - создание запроса на участие", userId, eventId);
        statsService.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
        
        ParticipationRequestDto participationRequest = participationRequestService.createRequest(userId, eventId);
        return ResponseEntity.status(HttpStatus.CREATED).body(participationRequest);
    }
    
    @PatchMapping("/{requestId}/cancel")
    @Operation(summary = "Отмена своего запроса на участие в событии")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(
            @Parameter(description = "ID пользователя") @PathVariable Long userId,
            @Parameter(description = "ID запроса") @PathVariable Long requestId,
            HttpServletRequest request) {
        log.info("PATCH /users/{}/requests/{}/cancel - отмена запроса", userId, requestId);
        statsService.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
        
        ParticipationRequestDto participationRequest = participationRequestService.cancelRequest(userId, requestId);
        return ResponseEntity.ok(participationRequest);
    }
}
