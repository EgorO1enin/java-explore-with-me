package ru.practicum.ewm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.request.NewCommentDto;
import ru.practicum.ewm.dto.request.UpdateCommentRequest;
import ru.practicum.ewm.dto.response.CommentDto;
import ru.practicum.ewm.service.CommentService;
import ru.practicum.ewm.service.StatsService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/users/{userId}")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Private: Комментарии", description = "Закрытый API для работы с комментариями")
public class PrivateCommentController {

    private final CommentService commentService;
    private final StatsService statsService;

    @PostMapping("/events/{eventId}/comments")
    @Operation(summary = "Создание комментария к событию")
    public ResponseEntity<CommentDto> createComment(
            @Parameter(description = "id пользователя") @PathVariable Long userId,
            @Parameter(description = "id события") @PathVariable Long eventId,
            @Valid @RequestBody NewCommentDto newCommentDto,
            HttpServletRequest request) {

        log.info("Создание комментария пользователем с id={} к событию с id={}", userId, eventId);

        // Сохраняем статистику
        statsService.saveHit("ewm-main-service", request.getRequestURI(),
                request.getRemoteAddr(), LocalDateTime.now());

        CommentDto comment = commentService.createComment(userId, eventId, newCommentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @GetMapping("/comments")
    @Operation(summary = "Получение комментариев пользователя")
    public ResponseEntity<Page<CommentDto>> getUserComments(
            @Parameter(description = "id пользователя") @PathVariable Long userId,
            @Parameter(description = "количество комментариев, которые нужно пропустить")
            @RequestParam(defaultValue = "0") int from,
            @Parameter(description = "количество комментариев в наборе")
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        log.info("Получение комментариев пользователя с id={}, from={}, size={}", userId, from, size);

        // Сохраняем статистику
        statsService.saveHit("ewm-main-service", request.getRequestURI(),
                request.getRemoteAddr(), LocalDateTime.now());

        Page<CommentDto> comments = commentService.getUserComments(userId, from, size);
        return ResponseEntity.ok(comments);
    }

    @PatchMapping("/comments/{commentId}")
    @Operation(summary = "Обновление комментария")
    public ResponseEntity<CommentDto> updateComment(
            @Parameter(description = "id пользователя") @PathVariable Long userId,
            @Parameter(description = "id комментария") @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentRequest updateCommentRequest,
            HttpServletRequest request) {

        log.info("Обновление комментария с id={} пользователем с id={}", commentId, userId);

        // Сохраняем статистику
        statsService.saveHit("ewm-main-service", request.getRequestURI(),
                request.getRemoteAddr(), LocalDateTime.now());

        CommentDto comment = commentService.updateComment(userId, commentId, updateCommentRequest);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/comments/{commentId}")
    @Operation(summary = "Удаление комментария")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "id пользователя") @PathVariable Long userId,
            @Parameter(description = "id комментария") @PathVariable Long commentId,
            HttpServletRequest request) {

        log.info("Удаление комментария с id={} пользователем с id={}", commentId, userId);

        // Сохраняем статистику
        statsService.saveHit("ewm-main-service", request.getRequestURI(),
                request.getRemoteAddr(), LocalDateTime.now());

        commentService.deleteComment(userId, commentId);
        return ResponseEntity.noContent().build();
    }
}

