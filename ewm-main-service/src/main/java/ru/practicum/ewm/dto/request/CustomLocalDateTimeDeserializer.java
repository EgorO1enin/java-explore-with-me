package ru.practicum.ewm.dto.request;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CustomLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private static final DateTimeFormatter FORMATTER_WITH_SPACE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter FORMATTER_WITH_T = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String dateString = p.getText();

        try {
            // Пробуем формат с пробелом (Newman)
            return LocalDateTime.parse(dateString, FORMATTER_WITH_SPACE);
        } catch (DateTimeParseException e1) {
            try {
                // Пробуем стандартный ISO формат
                return LocalDateTime.parse(dateString, FORMATTER_WITH_T);
            } catch (DateTimeParseException e2) {
                // Пробуем стандартный десериализатор
                return LocalDateTimeDeserializer.INSTANCE.deserialize(p, ctxt);
            }
        }
    }
}
