package ru.practicum.ewm.stats.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;

import java.util.List;

/**
 * Feign клиент для взаимодействия с сервисом статистики
 */
@FeignClient(name = "stats-service", url = "${stats.client.url:http://localhost:9090}")
public interface StatsFeignClient {

    /**
     * Сохранить информацию о запросе к эндпоинту
     *
     * @param endpointHit информация о запросе
     */
    @PostMapping("/hit")
    void saveHit(@RequestBody EndpointHit endpointHit);

    /**
     * Получить статистику посещений
     *
     * @param start дата и время начала диапазона (в формате "yyyy-MM-dd HH:mm:ss")
     * @param end дата и время конца диапазона (в формате "yyyy-MM-dd HH:mm:ss")
     * @param uris список URI для которых нужна статистика
     * @param unique учитывать только уникальные посещения (по IP)
     * @return список статистики
     */
    @GetMapping("/stats")
    List<ViewStats> getStats(@RequestParam("start") String start,
                           @RequestParam("end") String end,
                           @RequestParam(value = "uris", required = false) List<String> uris,
                           @RequestParam(value = "unique", defaultValue = "false") Boolean unique);
}
