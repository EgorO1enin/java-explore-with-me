package ru.practicum.ewm.stats.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;
import ru.practicum.ewm.stats.service.StatsService;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Tag(name = "Statistics", description = "API для работы со статистикой посещений")
public class StatsController {
    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @PostMapping("/hit")
    @Operation(
        summary = "Сохранение информации о запросе",
        description = "Сохранение информации о том, что к эндпоинту был запрос"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Информация сохранена"),
        @ApiResponse(
            responseCode = "400",
            description = "Запрос составлен некорректно",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<Void> hit(
            @Parameter(description = "Данные запроса", required = true)
            @Valid @RequestBody EndpointHit endpointHit) {
        statsService.saveHit(endpointHit);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/stats")
    @Operation(
        summary = "Получение статистики по посещениям",
        description = "Получение статистики по посещениям. Обратите внимание: значение даты и времени " +
                     "нужно закодировать (например используя java.net.URLEncoder.encode)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Статистика собрана",
            content = @Content(schema = @Schema(implementation = ViewStats.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Запрос составлен некорректно",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<List<ViewStats>> getStats(
            @Parameter(
                description = "Дата и время начала диапазона за который нужно выгрузить статистику (в формате \"yyyy-MM-dd HH:mm:ss\")",
                required = true,
                example = "2022-09-06 11:00:23"
            )
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,

            @Parameter(
                description = "Дата и время конца диапазона за который нужно выгрузить статистику (в формате \"yyyy-MM-dd HH:mm:ss\")",
                required = true,
                example = "2022-09-06 12:00:23"
            )
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,

            @Parameter(
                description = "Список URI для которых нужно выгрузить статистику",
                example = "/events/1"
            )
            @RequestParam(required = false) List<String> uris,

            @Parameter(
                description = "Нужно ли учитывать только уникальные посещения (только с уникальным IP)",
                example = "false"
            )
            @RequestParam(defaultValue = "false") Boolean unique) {

        List<ViewStats> stats = statsService.getStats(start, end, uris, unique);
        return ResponseEntity.ok(stats);
    }
}
