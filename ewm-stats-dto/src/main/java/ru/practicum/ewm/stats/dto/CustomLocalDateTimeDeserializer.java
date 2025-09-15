package ru.practicum.ewm.stats.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CustomLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    
    private static final DateTimeFormatter FORMATTER_WITH_SPACE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter FORMATTER_WITH_T = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    
    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String dateTimeString = p.getText();
        
        try {
            // Сначала пробуем формат с пробелом (как в спецификации)
            return LocalDateTime.parse(dateTimeString, FORMATTER_WITH_SPACE);
        } catch (DateTimeParseException e1) {
            try {
                // Если не получилось, пробуем ISO формат
                return LocalDateTime.parse(dateTimeString, FORMATTER_WITH_T);
            } catch (DateTimeParseException e2) {
                throw new IOException("Unable to parse date: " + dateTimeString, e2);
            }
        }
    }
}
