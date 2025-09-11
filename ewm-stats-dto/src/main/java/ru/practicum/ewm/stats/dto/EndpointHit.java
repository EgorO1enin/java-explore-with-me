package ru.practicum.ewm.stats.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Информация о запросе к эндпоинту")
public class EndpointHit {

    @Schema(description = "Идентификатор записи", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "App identifier cannot be blank")
    @Schema(description = "Идентификатор сервиса для которого записывается информация", example = "ewm-main-service", requiredMode = Schema.RequiredMode.REQUIRED)
    private String app;

    @NotBlank(message = "URI cannot be blank")
    @Schema(description = "URI для которого был осуществлен запрос", example = "/events/1", requiredMode = Schema.RequiredMode.REQUIRED)
    private String uri;

    @NotBlank(message = "IP address cannot be blank")
    @Schema(description = "IP-адрес пользователя, осуществившего запрос", example = "192.163.0.1", requiredMode = Schema.RequiredMode.REQUIRED)
    private String ip;

    @NotNull(message = "Timestamp cannot be null")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Schema(description = "Дата и время, когда был совершен запрос к эндпоинту",
            example = "2022-09-06T11:00:23.000Z",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime timestamp;
}