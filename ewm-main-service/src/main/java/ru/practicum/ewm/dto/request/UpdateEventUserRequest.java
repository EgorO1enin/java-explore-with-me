package ru.practicum.ewm.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.Location;

import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventUserRequest {
    
    @Size(min = 20, max = 2000)
    private String annotation;
    
    private Long category;
    
    @Size(min = 20, max = 7000)
    private String description;
    
    private LocalDateTime eventDate;
    
    private Location location;
    
    private Boolean paid;
    
    private Integer participantLimit;
    
    private Boolean requestModeration;
    
    private String stateAction; // SEND_TO_REVIEW, CANCEL_REVIEW
    
    @Size(min = 3, max = 120)
    private String title;
}
