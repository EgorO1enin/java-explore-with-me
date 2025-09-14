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
        log.info("🆕 POST /users/{}/events - создание события", userId);
        log.info("📊 Данные события: title={}, eventDate={}, category={}, paid={}",
                newEventDto.getTitle(), newEventDto.getEventDate(),
                newEventDto.getCategory(), newEventDto.getPaid());
        log.info("🌐 IP адрес: {}, User-Agent: {}", request.getRemoteAddr(), request.getHeader("User-Agent"));

        try {
            statsService.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
            log.info("✅ Статистика сохранена успешно");
        } catch (Exception e) {
            log.error("❌ Ошибка при сохранении статистики: {}", e.getMessage());
        }

        try {
            EventFullDto event = eventService.createEvent(userId, newEventDto);
            log.info("✅ Событие создано успешно: id={}, title={}, state={}",
                    event.getId(), event.getTitle(), event.getState());
            return ResponseEntity.status(HttpStatus.CREATED).body(event);
        } catch (Exception e) {
            log.error("❌ Ошибка при создании события: {}", e.getMessage(), e);
            throw e;
        }
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
        log.info("📋 GET /users/{}/events - получение событий пользователя", userId);
        log.info("📊 Параметры: from={}, size={}", from, size);
        log.info("🌐 IP адрес: {}, User-Agent: {}", request.getRemoteAddr(), request.getHeader("User-Agent"));

        try {
            statsService.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
            log.info("✅ Статистика сохранена успешно");
        } catch (Exception e) {
            log.error("❌ Ошибка при сохранении статистики: {}", e.getMessage());
        }

        try {
            List<EventShortDto> events = eventService.getUserEvents(userId, from, size);
            log.info("✅ Найдено событий пользователя {}: {}", userId, events.size());
            if (!events.isEmpty()) {
                log.info("📋 Первое событие: id={}, title={}, state={}",
                        events.get(0).getId(), events.get(0).getTitle());
            }
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            log.error("❌ Ошибка при получении событий пользователя {}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{eventId}")
    @Operation(summary = "Получение полной информации о событии добавленном текущим пользователем")
    public ResponseEntity<EventFullDto> getUserEvent(
            @Parameter(description = "ID пользователя") @PathVariable Long userId,
            @Parameter(description = "ID события") @PathVariable Long eventId,
            HttpServletRequest request) {
        log.info("🔍 GET /users/{}/events/{} - получение события пользователя", userId, eventId);
        log.info("🌐 IP адрес: {}, User-Agent: {}", request.getRemoteAddr(), request.getHeader("User-Agent"));

        try {
            statsService.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
            log.info("✅ Статистика сохранена успешно");
        } catch (Exception e) {
            log.error("❌ Ошибка при сохранении статистики: {}", e.getMessage());
        }

        try {
            EventFullDto event = eventService.getUserEvent(userId, eventId);
            log.info("✅ Событие найдено: id={}, title={}, state={}, confirmedRequests={}",
                    event.getId(), event.getTitle(), event.getState(), event.getConfirmedRequests());
            return ResponseEntity.ok(event);
        } catch (Exception e) {
            log.error("❌ Ошибка при получении события {} пользователя {}: {}", eventId, userId, e.getMessage(), e);
            throw e;
        }
    }

    @PatchMapping("/{eventId}")
    @Operation(summary = "Изменение события добавленного текущим пользователем")
    public ResponseEntity<EventFullDto> updateUserEvent(
            @Parameter(description = "ID пользователя") @PathVariable Long userId,
            @Parameter(description = "ID события") @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest,
            HttpServletRequest request) {
        log.info("🔧 PATCH /users/{}/events/{} - обновление события пользователя", userId, eventId);
        log.info("📊 Данные для обновления: {}", updateEventUserRequest);
        log.info("🌐 IP адрес: {}, User-Agent: {}", request.getRemoteAddr(), request.getHeader("User-Agent"));

        try {
            statsService.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
            log.info("✅ Статистика сохранена успешно");
        } catch (Exception e) {
            log.error("❌ Ошибка при сохранении статистики: {}", e.getMessage());
        }

        try {
            EventFullDto event = eventService.updateUserEvent(userId, eventId, updateEventUserRequest);
            log.info("✅ Событие {} пользователя {} успешно обновлено: title={}, state={}",
                    eventId, userId, event.getTitle(), event.getState());
            return ResponseEntity.ok(event);
        } catch (Exception e) {
            log.error("❌ Ошибка при обновлении события {} пользователя {}: {}", eventId, userId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{eventId}/requests")
    @Operation(summary = "Получение информации о запросах на участие в событии текущего пользователя")
    public ResponseEntity<List<ParticipationRequestDto>> getEventParticipants(
            @Parameter(description = "ID пользователя") @PathVariable Long userId,
            @Parameter(description = "ID события") @PathVariable Long eventId,
            HttpServletRequest request) {
        log.info("📋 GET /users/{}/events/{}/requests - получение запросов на участие в событии", userId, eventId);
        log.info("🌐 IP адрес: {}, User-Agent: {}", request.getRemoteAddr(), request.getHeader("User-Agent"));

        try {
            statsService.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
            log.info("✅ Статистика сохранена успешно");
        } catch (Exception e) {
            log.error("❌ Ошибка при сохранении статистики: {}", e.getMessage());
        }

        try {
            List<ParticipationRequestDto> requests = participationRequestService.getEventParticipants(userId, eventId);
            log.info("✅ Найдено запросов на участие в событии {}: {}", eventId, requests.size());
            if (!requests.isEmpty()) {
                log.info("📋 Первый запрос: id={}, status={}, requester={}",
                        requests.get(0).getId(), requests.get(0).getStatus(), requests.get(0).getRequester());
            }
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            log.error("❌ Ошибка при получении запросов на участие в событии {} пользователя {}: {}",
                    eventId, userId, e.getMessage(), e);
            throw e;
        }
    }

    @PatchMapping("/{eventId}/requests")
    @Operation(summary = "Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя")
    public ResponseEntity<EventRequestStatusUpdateResult> changeRequestStatus(
            @Parameter(description = "ID пользователя") @PathVariable Long userId,
            @Parameter(description = "ID события") @PathVariable Long eventId,
            @Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest,
            HttpServletRequest request) {
        log.info("🔧 PATCH /users/{}/events/{}/requests - изменение статуса запросов", userId, eventId);
        log.info("📊 Данные для обновления: {}", eventRequestStatusUpdateRequest);
        log.info("🌐 IP адрес: {}, User-Agent: {}", request.getRemoteAddr(), request.getHeader("User-Agent"));

        try {
            statsService.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
            log.info("✅ Статистика сохранена успешно");
        } catch (Exception e) {
            log.error("❌ Ошибка при сохранении статистики: {}", e.getMessage());
        }

        try {
            EventRequestStatusUpdateResult result = participationRequestService.changeRequestStatus(
                    userId, eventId, eventRequestStatusUpdateRequest);
            log.info("✅ Статус запросов изменен успешно: confirmed={}, rejected={}",
                    result.getConfirmedRequests().size(), result.getRejectedRequests().size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("❌ Ошибка при изменении статуса запросов для события {} пользователя {}: {}",
                    eventId, userId, e.getMessage(), e);
            throw e;
        }
    }
}
