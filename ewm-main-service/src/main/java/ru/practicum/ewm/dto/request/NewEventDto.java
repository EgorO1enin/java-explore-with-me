package ru.practicum.ewm.dto.request;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.Location;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
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
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime eventDate;
    
    @NotNull
    private Location location;
    
    @Builder.Default
    private Boolean paid = false;
    
    @Builder.Default
    @Min(value = 1, message = "Лимит участников не может быть отрицательным")
    private Integer participantLimit = 0;
    
    @Builder.Default
    private Boolean requestModeration = true;
    
    @NotBlank
    @Size(min = 3, max = 120)
    private String title;
    
    @JsonSetter("paid")
    public void setPaid(Object paid) {
        this.paid = convertToBoolean(paid);
    }
    
    @JsonSetter("participantLimit")
    public void setParticipantLimit(Object participantLimit) {
        this.participantLimit = convertToInteger(participantLimit);
    }
    
    @JsonSetter("requestModeration")
    public void setRequestModeration(Object requestModeration) {
        this.requestModeration = convertToBoolean(requestModeration);
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
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
}
