package ru.practicum.ewm.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompilationRequest {
    
    @Size(min = 1, max = 50)
    private String title;
    
    private Boolean pinned;
    
    private List<Long> events;
}
