package ru.practicum.ewm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.response.CompilationDto;
import ru.practicum.ewm.service.CompilationService;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Public: Подборки событий", description = "Публичный API для работы с подборками событий")
public class PublicCompilationController {

    private final CompilationService compilationService;

    @GetMapping
    @Operation(summary = "Получение подборок событий")
    public ResponseEntity<List<CompilationDto>> getCompilations(
            @Parameter(description = "искать только закрепленные/не закрепленные подборки")
            @RequestParam(required = false) Boolean pinned,
            @Parameter(description = "количество элементов, которые нужно пропустить")
            @RequestParam(defaultValue = "0") int from,
            @Parameter(description = "количество элементов в наборе")
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /compilations - получение подборок: pinned={}, from={}, size={}", pinned, from, size);
        List<CompilationDto> compilations = compilationService.getCompilations(pinned, from, size);
        return ResponseEntity.ok(compilations);
    }

    @GetMapping("/{compId}")
    @Operation(summary = "Получение подборки событий по его id")
    public ResponseEntity<CompilationDto> getCompilation(
            @Parameter(description = "id подборки") @PathVariable Long compId) {
        log.info("GET /compilations/{} - получение подборки", compId);
        CompilationDto compilationDto = compilationService.getCompilation(compId);
        return ResponseEntity.ok(compilationDto);
    }
}
