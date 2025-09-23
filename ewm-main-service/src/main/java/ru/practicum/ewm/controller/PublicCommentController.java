package ru.practicum.ewm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.response.CommentDto;
import ru.practicum.ewm.service.CommentService;
import ru.practicum.ewm.service.StatsService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Public: Комментарии", description = "Публичный API для работы с комментариями")
public class PublicCommentController {

    private final CommentService commentService;
    private final StatsService statsService;

    @GetMapping("/{eventId}/comments")
    @Operation(summary = "Получение комментариев к событию")
    public ResponseEntity<Page<CommentDto>> getCommentsByEvent(
            @Parameter(description = "id события") @PathVariable Long eventId,
            @Parameter(description = "количество комментариев, которые нужно пропустить")
            @RequestParam(defaultValue = "0") int from,
            @Parameter(description = "количество комментариев в наборе")
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        log.info("Получение комментариев к событию с id={}, from={}, size={}", eventId, from, size);

        // Сохраняем статистику
        statsService.saveHit("ewm-main-service", request.getRequestURI(),
                request.getRemoteAddr(), LocalDateTime.now());

        Page<CommentDto> comments = commentService.getCommentsByEvent(eventId, from, size);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/comments/search")
    @Operation(summary = "Поиск комментариев по тексту")
    public ResponseEntity<Page<CommentDto>> searchComments(
            @Parameter(description = "текст для поиска") @RequestParam String text,
            @Parameter(description = "количество комментариев, которые нужно пропустить")
            @RequestParam(defaultValue = "0") int from,
            @Parameter(description = "количество комментариев в наборе")
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        log.info("Поиск комментариев по тексту: {}, from={}, size={}", text, from, size);

        // Сохраняем статистику
        statsService.saveHit("ewm-main-service", request.getRequestURI(),
                request.getRemoteAddr(), LocalDateTime.now());

        Page<CommentDto> comments = commentService.searchComments(text, from, size);
        return ResponseEntity.ok(comments);
    }
}

