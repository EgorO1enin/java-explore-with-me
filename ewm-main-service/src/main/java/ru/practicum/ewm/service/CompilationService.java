package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.response.CompilationDto;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationService {
    
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        log.info("Получение подборок: pinned={}, from={}, size={}", pinned, from, size);
        return List.of(); // Заглушка
    }
    
    public CompilationDto getCompilationById(Long compId) {
        log.info("Получение подборки с ID: {}", compId);
        return CompilationDto.builder()
                .id(compId)
                .title("Test Compilation")
                .pinned(false)
                .events(List.of())
                .build(); // Заглушка
    }
}
