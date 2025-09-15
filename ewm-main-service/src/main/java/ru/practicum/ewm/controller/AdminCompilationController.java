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
import ru.practicum.ewm.dto.request.NewCompilationDto;
import ru.practicum.ewm.dto.request.UpdateCompilationRequest;
import ru.practicum.ewm.dto.response.CompilationDto;
import ru.practicum.ewm.service.CompilationService;
import ru.practicum.ewm.service.StatsService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin: Подборки событий", description = "API для работы с подборками событий")
public class AdminCompilationController {

    private final CompilationService compilationService;
    private final StatsService statsService;

    @PostMapping
    @Operation(summary = "Добавление новой подборки (подборка может не содержать событий)")
    public ResponseEntity<CompilationDto> createCompilation(
            @Valid @RequestBody NewCompilationDto newCompilationDto,
            HttpServletRequest request) {
        log.info("POST /admin/compilations - создание подборки");
        statsService.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());

        CompilationDto compilation = compilationService.createCompilation(newCompilationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(compilation);
    }

    @DeleteMapping("/{compId}")
    @Operation(summary = "Удаление подборки")
    public ResponseEntity<Void> deleteCompilation(
            @Parameter(description = "id подборки") @PathVariable Long compId,
            HttpServletRequest request) {
        log.info("DELETE /admin/compilations/{} - удаление подборки", compId);
        statsService.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());

        compilationService.deleteCompilation(compId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{compId}")
    @Operation(summary = "Обновить информацию о подборке")
    public ResponseEntity<CompilationDto> updateCompilation(
            @Parameter(description = "id подборки") @PathVariable Long compId,
            @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest,
            HttpServletRequest request) {
        log.info("🔧 PATCH /admin/compilations/{} - обновление подборки", compId);
        log.info("📊 Данные для обновления: {}", updateCompilationRequest);
        log.info("🌐 IP адрес: {}, User-Agent: {}", request.getRemoteAddr(), request.getHeader("User-Agent"));

        try {
            statsService.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
            log.info("✅ Статистика сохранена успешно");
        } catch (Exception e) {
            log.error("❌ Ошибка при сохранении статистики: {}", e.getMessage());
        }

        try {
            CompilationDto compilation = compilationService.updateCompilation(compId, updateCompilationRequest);
            log.info("✅ Подборка {} успешно обновлена: title={}, pinned={}, events={}",
                    compId, compilation.getTitle(), compilation.getPinned(),
                    compilation.getEvents() != null ? compilation.getEvents().size() : 0);
            return ResponseEntity.ok(compilation);
        } catch (Exception e) {
            log.error("❌ Ошибка при обновлении подборки {}: {}", compId, e.getMessage(), e);
            throw e;
        }
    }
}
