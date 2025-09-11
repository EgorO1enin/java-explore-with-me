package ru.practicum.ewm.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.Location;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {
    
    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;
    
    @NotNull
    private Long category;
    
    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;
    
    @NotNull
    private LocalDateTime eventDate;
    
    @NotNull
    private Location location;
    
    private Boolean paid = false;
    
    private Integer participantLimit = 0;
    
    private Boolean requestModeration = true;
    
    @NotBlank
    @Size(min = 3, max = 120)
    private String title;
    
    @JsonCreator
    public NewEventDto(
            @JsonProperty("annotation") String annotation,
            @JsonProperty("category") Long category,
            @JsonProperty("description") String description,
            @JsonProperty("eventDate") LocalDateTime eventDate,
            @JsonProperty("location") Location location,
            @JsonProperty("paid") Object paid,
            @JsonProperty("participantLimit") Object participantLimit,
            @JsonProperty("requestModeration") Object requestModeration,
            @JsonProperty("title") String title) {
        this.annotation = annotation;
        this.category = category;
        this.description = description;
        this.eventDate = eventDate;
        this.location = location;
        this.paid = convertToBoolean(paid);
        this.participantLimit = convertToInteger(participantLimit);
        this.requestModeration = convertToBoolean(requestModeration);
        this.title = title;
    }
    
    private Boolean convertToBoolean(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return false;
    }
    
    private Integer convertToInteger(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof String) {
            return Integer.parseInt((String) value);
        }
        return 0;
    }
}
