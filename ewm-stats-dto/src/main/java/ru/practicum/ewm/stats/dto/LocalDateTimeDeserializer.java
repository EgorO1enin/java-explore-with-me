package ru.practicum.ewm.stats.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Гибкий десериализатор для LocalDateTime, поддерживающий несколько форматов
 */
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private static final DateTimeFormatter[] FORMATTERS = {
        // ISO 8601 с миллисекундами и Z
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),
        // ISO 8601 без миллисекунд и с Z
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"),
        // Простой формат
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
        // ISO 8601 без Z
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
        // ISO 8601 с миллисекундами без Z
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
    };

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        String dateTimeString = node.asText();

        if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
            return null;
        }
        // Пробуем каждый формат
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return LocalDateTime.parse(dateTimeString, formatter);
            } catch (DateTimeParseException e) {
                // Продолжаем пробовать следующий формат
            }
        }
        throw new IOException("Не удалось распарсить дату: " + dateTimeString +
                            ". Поддерживаемые форматы: yyyy-MM-dd'T'HH:mm:ss.SSS'Z', " +
                            "yyyy-MM-dd'T'HH:mm:ss'Z', yyyy-MM-dd HH:mm:ss");
    }
}
