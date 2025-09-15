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
import ru.practicum.ewm.exception.BadRequestException;
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
        log.info("📋 GET /users/{}/requests - получение запросов пользователя", userId);
        log.info("🌐 IP адрес: {}, User-Agent: {}", request.getRemoteAddr(), request.getHeader("User-Agent"));

        // Проверяем обязательные параметры
        if (userId == null || userId <= 0) {
            log.error("❌ Ошибка валидации: userId не указан или некорректный: {}", userId);
            throw new BadRequestException("ID пользователя должен быть указан и больше 0");
        }

        try {
            statsService.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
            log.info("✅ Статистика сохранена успешно");
        } catch (Exception e) {
            log.error("❌ Ошибка при сохранении статистики: {}", e.getMessage());
        }

        try {
            List<ParticipationRequestDto> requests = participationRequestService.getUserRequests(userId);
            log.info("✅ Найдено запросов пользователя {}: {}", userId, requests.size());
            if (!requests.isEmpty()) {
                log.info("📋 Первый запрос: id={}, status={}, event={}",
                        requests.get(0).getId(), requests.get(0).getStatus(), requests.get(0).getEvent());
            }
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            log.error("❌ Ошибка при получении запросов пользователя {}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping
    @Operation(summary = "Добавление запроса от текущего пользователя на участие в событии")
    public ResponseEntity<ParticipationRequestDto> createRequest(
            @Parameter(description = "ID пользователя") @PathVariable Long userId,
            @Parameter(description = "ID события") @RequestParam(required = false) Long eventId,
            HttpServletRequest request) {
        log.info("🆕 POST /users/{}/requests?eventId={} - создание запроса на участие", userId, eventId);
        log.info("🌐 IP адрес: {}, User-Agent: {}", request.getRemoteAddr(), request.getHeader("User-Agent"));

        // Проверяем обязательные параметры
        if (userId == null || userId <= 0) {
            log.error("❌ Ошибка валидации: userId не указан или некорректный: {}", userId);
            throw new BadRequestException("ID пользователя должен быть указан и больше 0");
        }

        if (eventId == null || eventId < 0) {
            log.error("❌ Ошибка валидации: eventId не указан или некорректный: {}", eventId);
            throw new BadRequestException("ID события должен быть указан и не может быть отрицательным");
        }

        try {
            statsService.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
            log.info("✅ Статистика сохранена успешно");
        } catch (Exception e) {
            log.error("❌ Ошибка при сохранении статистики: {}", e.getMessage());
        }

        try {
            ParticipationRequestDto participationRequest = participationRequestService.createRequest(userId, eventId);
            log.info("✅ Запрос на участие создан успешно: id={}, status={}, event={}",
                    participationRequest.getId(), participationRequest.getStatus(), participationRequest.getEvent());
            return ResponseEntity.status(HttpStatus.CREATED).body(participationRequest);
        } catch (Exception e) {
            log.error("❌ Ошибка при создании запроса на участие пользователя {} в событии {}: {}",
                    userId, eventId, e.getMessage(), e);
            throw e;
        }
    }

    @PatchMapping("/{requestId}/cancel")
    @Operation(summary = "Отмена своего запроса на участие в событии")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(
            @Parameter(description = "ID пользователя") @PathVariable Long userId,
            @Parameter(description = "ID запроса") @PathVariable Long requestId,
            HttpServletRequest request) {
        log.info("❌ PATCH /users/{}/requests/{}/cancel - отмена запроса", userId, requestId);
        log.info("🌐 IP адрес: {}, User-Agent: {}", request.getRemoteAddr(), request.getHeader("User-Agent"));

        // Проверяем обязательные параметры
        if (userId == null || userId <= 0) {
            log.error("❌ Ошибка валидации: userId не указан или некорректный: {}", userId);
            throw new BadRequestException("ID пользователя должен быть указан и больше 0");
        }

        if (requestId == null || requestId <= 0) {
            log.error("❌ Ошибка валидации: requestId не указан или некорректный: {}", requestId);
            throw new BadRequestException("ID запроса должен быть указан и больше 0");
        }

        try {
            statsService.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
            log.info("✅ Статистика сохранена успешно");
        } catch (Exception e) {
            log.error("❌ Ошибка при сохранении статистики: {}", e.getMessage());
        }

        try {
            ParticipationRequestDto participationRequest = participationRequestService.cancelRequest(userId, requestId);
            log.info("✅ Запрос {} пользователя {} успешно отменен: status={}",
                    requestId, userId, participationRequest.getStatus());
            return ResponseEntity.ok(participationRequest);
        } catch (Exception e) {
            log.error("❌ Ошибка при отмене запроса {} пользователя {}: {}",
                    requestId, userId, e.getMessage(), e);
            throw e;
        }
    }
}
