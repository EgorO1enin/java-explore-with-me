package ru.practicum.ewm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.request.UpdateCommentAdminRequest;
import ru.practicum.ewm.dto.response.CommentDto;
import ru.practicum.ewm.service.CommentService;
import ru.practicum.ewm.service.StatsService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin: Комментарии", description = "API для работы с комментариями администратора")
public class AdminCommentController {

    private final CommentService commentService;
    private final StatsService statsService;

    @GetMapping
    @Operation(summary = "Получение комментариев для модерации")
    public ResponseEntity<Page<CommentDto>> getCommentsForModeration(
            @Parameter(description = "количество комментариев, которые нужно пропустить")
            @RequestParam(defaultValue = "0") int from,
            @Parameter(description = "количество комментариев в наборе")
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        log.info("Получение комментариев для модерации, from={}, size={}", from, size);

        // Сохраняем статистику
        statsService.saveHit("ewm-main-service", request.getRequestURI(),
                request.getRemoteAddr(), LocalDateTime.now());

        Page<CommentDto> comments = commentService.getCommentsForModeration(from, size);
        return ResponseEntity.ok(comments);
    }

    @PatchMapping("/{commentId}")
    @Operation(summary = "Модерация комментария")
    public ResponseEntity<CommentDto> moderateComment(
            @Parameter(description = "id комментария") @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentAdminRequest updateCommentAdminRequest,
            HttpServletRequest request) {

        log.info("Модерация комментария с id={}, статус={}", commentId, updateCommentAdminRequest.getStatus());

        // Сохраняем статистику
        statsService.saveHit("ewm-main-service", request.getRequestURI(),
                request.getRemoteAddr(), LocalDateTime.now());

        CommentDto comment = commentService.moderateComment(commentId, updateCommentAdminRequest);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "Удаление комментария администратором")
    public ResponseEntity<Void> deleteCommentByAdmin(
            @Parameter(description = "id комментария") @PathVariable Long commentId,
            HttpServletRequest request) {

        log.info("Удаление комментария с id={} администратором", commentId);

        // Сохраняем статистику
        statsService.saveHit("ewm-main-service", request.getRequestURI(),
                request.getRemoteAddr(), LocalDateTime.now());

        commentService.deleteCommentByAdmin(commentId);
        return ResponseEntity.noContent().build();
    }
}

