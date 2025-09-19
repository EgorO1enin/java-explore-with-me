package ru.practicum.ewm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.request.NewEventDto;
import ru.practicum.ewm.dto.request.UpdateEventUserRequest;
import ru.practicum.ewm.dto.response.EventFullDto;
import ru.practicum.ewm.dto.response.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.response.EventShortDto;
import ru.practicum.ewm.dto.response.ParticipationRequestDto;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.ParticipationRequestService;
import ru.practicum.ewm.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Private: События", description = "API для работы с событиями авторизованных пользователей")
public class PrivateEventController {

    private final EventService eventService;
    private final ParticipationRequestService participationRequestService;
    private final StatsService statsService;

    @PostMapping
    @Operation(summary = "Создание нового события")
    public ResponseEntity<EventFullDto> createEvent(
            @Parameter(description = "ID пользователя") @PathVariable Long userId,
            @Valid @RequestBody NewEventDto newEventDto,
            HttpServletRequest request) {
        log.info("POST /users/{}/events - создание события", userId);

        statsService.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now());
        EventFullDto event = eventService.createEvent(userId, newEventDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    @GetMapping
    @Operation(summary = "Получение событий, добавленных текущим пользователем")
    public ResponseEntity<List<EventShortDto>> getUserEvents(
            @Parameter(description = "ID пользователя") @PathVariable Long userId,
            @Parameter(description = "Количество элементов, которые нужно пропустить для формирования текущего набора")
            @RequestParam(defaultValue = "0") int from,
            @Parameter(description = "Количество элементов в наборе")
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        log.info("GET /users/{}/events - получение событий пользователя", userId);

        statsService.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now());
        List<EventShortDto> events = eventService.getUserEvents(userId, from, size);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{eventId}")
    @Operation(summary = "Получение полной информации о событии добавленном текущим пользователем")
    public ResponseEntity<EventFullDto> getUserEvent(
            @Parameter(description = "ID пользователя") @PathVariable Long userId,
            @Parameter(description = "ID события") @PathVariable Long eventId,
            HttpServletRequest request) {
        log.info("GET /users/{}/events/{} - получение события пользователя", userId, eventId);

        statsService.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now());
        EventFullDto event = eventService.getUserEvent(userId, eventId);
        return ResponseEntity.ok(event);
    }

    @PatchMapping("/{eventId}")
    @Operation(summary = "Изменение события добавленного текущим пользователем")
    public ResponseEntity<EventFullDto> updateUserEvent(
            @Parameter(description = "ID пользователя") @PathVariable Long userId,
            @Parameter(description = "ID события") @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest,
            HttpServletRequest request) {
        log.info("PATCH /users/{}/events/{} - обновление события пользователя", userId, eventId);

        statsService.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now());
        EventFullDto event = eventService.updateUserEvent(userId, eventId, updateEventUserRequest);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/{eventId}/requests")
    @Operation(summary = "Получение информации о запросах на участие в событии текущего пользователя")
    public ResponseEntity<List<ParticipationRequestDto>> getEventParticipants(
            @Parameter(description = "ID пользователя") @PathVariable Long userId,
            @Parameter(description = "ID события") @PathVariable Long eventId,
            HttpServletRequest request) {
        log.info("GET /users/{}/events/{}/requests - получение запросов на участие в событии", userId, eventId);

        statsService.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now());
        List<ParticipationRequestDto> requests = participationRequestService.getEventParticipants(userId, eventId);
        return ResponseEntity.ok(requests);
    }

    @PatchMapping("/{eventId}/requests")
    @Operation(summary = "Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя")
    public ResponseEntity<EventRequestStatusUpdateResult> changeRequestStatus(
            @Parameter(description = "ID пользователя") @PathVariable Long userId,
            @Parameter(description = "ID события") @PathVariable Long eventId,
            @Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest,
            HttpServletRequest request) {
        log.info("PATCH /users/{}/events/{}/requests - изменение статуса запросов", userId, eventId);

        statsService.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now());
        EventRequestStatusUpdateResult result = participationRequestService.changeRequestStatus(
                userId, eventId, eventRequestStatusUpdateRequest);
        return ResponseEntity.ok(result);
    }
}
