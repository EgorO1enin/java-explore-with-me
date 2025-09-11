package ru.practicum.ewm.dto.request;

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
}
