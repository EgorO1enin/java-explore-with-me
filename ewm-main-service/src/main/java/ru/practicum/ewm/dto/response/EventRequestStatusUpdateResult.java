package ru.practicum.ewm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateResult {
    
    private List<ParticipationRequestDto> confirmedRequests;
    
    private List<ParticipationRequestDto> rejectedRequests;
}
