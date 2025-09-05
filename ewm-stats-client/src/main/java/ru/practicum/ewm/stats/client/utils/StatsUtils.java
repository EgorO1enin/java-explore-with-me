package ru.practicum.ewm.stats.client.utils;

import ru.practicum.ewm.stats.dto.EndpointHit;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * Утилитарные методы для работы со статистикой
 */
public class StatsUtils {

    private StatsUtils() {
        // Утилитарный класс
    }

    /**
     * Создать объект EndpointHit из HTTP запроса
     *
     * @param request HTTP запрос
     * @param appName название приложения
     * @return объект EndpointHit
     */
    public static EndpointHit createEndpointHit(HttpServletRequest request, String appName) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp(appName);
        endpointHit.setUri(request.getRequestURI());
        endpointHit.setIp(getClientIpAddress(request));
        endpointHit.setTimestamp(LocalDateTime.now());
        return endpointHit;
    }

    /**
     * Получить IP адрес клиента из HTTP запроса
     *
     * @param request HTTP запрос
     * @return IP адрес клиента
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader != null && !xForwardedForHeader.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedForHeader)) {
            return xForwardedForHeader.split(",")[0].trim();
        }

        String xRealIpHeader = request.getHeader("X-Real-IP");
        if (xRealIpHeader != null && !xRealIpHeader.isEmpty() && !"unknown".equalsIgnoreCase(xRealIpHeader)) {
            return xRealIpHeader;
        }

        return request.getRemoteAddr();
    }
}
