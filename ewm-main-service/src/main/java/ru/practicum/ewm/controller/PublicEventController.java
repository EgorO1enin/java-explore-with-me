package ru.practicum.ewm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.response.EventFullDto;
import ru.practicum.ewm.dto.response.EventShortDto;
import ru.practicum.ewm.model.enums.EventSortType;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.StatsService;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Public: События", description = "Публичный API для работы с событиями")
public class PublicEventController {

    private final EventService eventService;
    private final StatsService statsService;

    @GetMapping
    @Operation(summary = "Получение событий с возможностью фильтрации")
    public ResponseEntity<List<EventShortDto>> getEvents(
            @Parameter(description = "текст для поиска в содержимом аннотации и подробном описании события")
            @RequestParam(required = false) String text,
            @Parameter(description = "список идентификаторов категорий в которых будет вестись поиск")
            @RequestParam(required = false) List<Long> categories,
            @Parameter(description = "поиск только платных/бесплатных событий")
            @RequestParam(required = false) Boolean paid,
            @Parameter(description = "дата и время не раньше которых должно произойти событие")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @Parameter(description = "дата и время не позже которых должно произойти событие")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @Parameter(description = "только события у которых не исчерпан лимит запросов на участие")
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @Parameter(description = "Вариант сортировки: по дате события или по количеству просмотров")
            @RequestParam(required = false) EventSortType sort,
            @Parameter(description = "количество событий, которые нужно пропустить")
            @RequestParam(defaultValue = "0") int from,
            @Parameter(description = "количество событий в наборе")
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        log.info("🌐 GET /events - получение публичных событий");
        log.info("📊 Параметры: text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}, onlyAvailable={}, sort={}, from={}, size={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        log.info("🌐 IP адрес: {}, User-Agent: {}", request.getRemoteAddr(), request.getHeader("User-Agent"));

        try {
            statsService.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
            log.info("✅ Статистика сохранена успешно");
        } catch (Exception e) {
            log.error("❌ Ошибка при сохранении статистики: {}", e.getMessage());
        }

        try {
            List<EventShortDto> events = eventService.getPublicEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
            log.info("✅ Найдено публичных событий: {}", events.size());
            if (!events.isEmpty()) {
                log.info("📋 Первое событие: id={}, title={}, views={}, confirmedRequests={}",
                        events.get(0).getId(), events.get(0).getTitle(),
                        events.get(0).getViews(), events.get(0).getConfirmedRequests());
            }
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            log.error("❌ Ошибка при получении публичных событий: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение подробной информации об опубликованном событии по его идентификатору")
    public ResponseEntity<EventFullDto> getEvent(
            @Parameter(description = "id события") @PathVariable Long id,
            HttpServletRequest request) {
        log.info("🔍 GET /events/{} - получение публичного события", id);
        log.info("🌐 IP адрес: {}, User-Agent: {}", request.getRemoteAddr(), request.getHeader("User-Agent"));

        try {
            EventFullDto eventDto = eventService.getPublicEvent(id);

            // Сохраняем статистику ПОСЛЕ получения события
            try {
                statsService.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
                log.info("✅ Статистика сохранена успешно");
            } catch (Exception e) {
                log.error("❌ Ошибка при сохранении статистики: {}", e.getMessage());
            }

            log.info("✅ Публичное событие найдено: id={}, title={}, state={}, views={}, confirmedRequests={}",
                    eventDto.getId(), eventDto.getTitle(), eventDto.getState(),
                    eventDto.getViews(), eventDto.getConfirmedRequests());
            return ResponseEntity.ok(eventDto);
        } catch (Exception e) {
            log.error("❌ Ошибка при получении публичного события {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}