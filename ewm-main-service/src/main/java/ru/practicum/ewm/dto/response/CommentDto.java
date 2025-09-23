package ru.practicum.ewm.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.enums.CommentStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String text;
    private UserShortDto author;
    private Long event;
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    private LocalDateTime created;
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    private LocalDateTime updated;
    private CommentStatus status;
}
