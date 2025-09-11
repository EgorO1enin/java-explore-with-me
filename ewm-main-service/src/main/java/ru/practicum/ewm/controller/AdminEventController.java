package ru.practicum.ewm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.request.UpdateEventAdminRequest;
import ru.practicum.ewm.dto.response.EventFullDto;
import ru.practicum.ewm.model.enums.EventState;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin: События", description = "API для работы с событиями администратора")
public class AdminEventController {
    
    private final EventService eventService;
    private final StatsService statsService;
    
    @GetMapping
    @Operation(summary = "Поиск событий")
    public ResponseEntity<List<EventFullDto>> getAdminEvents(
            @Parameter(description = "Список id пользователей, чьи события нужно найти") 
            @RequestParam(required = false) List<Long> users,
            @Parameter(description = "Список состояний в которых находятся искомые события") 
            @RequestParam(required = false) List<EventState> states,
            @Parameter(description = "Список id категорий в которых будет вестись поиск") 
            @RequestParam(required = false) List<Long> categories,
            @Parameter(description = "Дата и время не раньше которых должно произойти событие") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @Parameter(description = "Дата и время не позже которых должно произойти событие") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @Parameter(description = "Количество элементов, которые нужно пропустить для формирования текущего набора") 
            @RequestParam(defaultValue = "0") int from,
            @Parameter(description = "Количество элементов в наборе") 
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        log.info("GET /admin/events - получение событий администратором");
        statsService.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
        
        List<EventFullDto> events = eventService.getAdminEvents(users, states, categories, rangeStart, rangeEnd, from, size);
        return ResponseEntity.ok(events);
    }
    
    @PatchMapping("/{eventId}")
    @Operation(summary = "Редактирование данных события и его статуса (отклонение/публикация)")
    public ResponseEntity<EventFullDto> updateAdminEvent(
            @Parameter(description = "ID события") @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest,
            HttpServletRequest request) {
        log.info("PATCH /admin/events/{} - обновление события администратором", eventId);
        statsService.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
        
        EventFullDto event = eventService.updateAdminEvent(eventId, updateEventAdminRequest);
        return ResponseEntity.ok(event);
    }
}
