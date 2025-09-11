package ru.practicum.ewm.stats.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Schema(description = "Статистика просмотров")
public class ViewStats {

    @Schema(description = "Название сервиса", example = "ewm-main-service")
    private String app;

    @Schema(description = "URI сервиса", example = "/events/1")
    private String uri;

    @Schema(description = "Количество просмотров", example = "6")
    private Long hits;
}
