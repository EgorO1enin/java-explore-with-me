package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.request.NewCompilationDto;
import ru.practicum.ewm.dto.request.UpdateCompilationRequest;
import ru.practicum.ewm.dto.response.CompilationDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;

    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        log.info("Получение подборок: pinned={}, from={}, size={}", pinned, from, size);

        Pageable pageable = PageRequest.of(from / size, size);
        Page<Compilation> compilations;

        if (pinned != null) {
            compilations = compilationRepository.findByPinned(pinned, pageable);
        } else {
            compilations = compilationRepository.findAll(pageable);
        }

        List<Compilation> compilationList = compilations.getContent();
        log.info("Найдено подборок: {}", compilationList != null ? compilationList.size() : "null");

        if (compilationList == null) {
            compilationList = new ArrayList<>();
            log.warn("compilations.getContent() вернул null, создаем пустой список");
        }

        List<CompilationDto> result = compilationMapper.toCompilationDtoList(compilationList);
        log.info("Результат маппинга: {}", result != null ? result.size() + " элементов" : "null");

        // Дополнительная защита: убеждаемся, что result не null
        if (result == null) {
            result = new ArrayList<>();
            log.warn("compilationMapper.toCompilationDtoList() вернул null, создаем пустой список");
        }

        // Дополнительная защита: убеждаемся, что events в каждом CompilationDto не null
        for (CompilationDto compilationDto : result) {
            if (compilationDto.getEvents() == null) {
                compilationDto.setEvents(new ArrayList<>());
                log.warn("CompilationDto {} имеет null events, устанавливаем пустой список", compilationDto.getId());
            }
        }

        return result;
    }

    public CompilationDto getCompilation(Long compId) {
        log.info("Получение подборки с ID: {}", compId);

        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка с ID " + compId + " не найдена"));

        CompilationDto result = compilationMapper.toCompilationDto(compilation);

        // Дополнительная защита: убеждаемся, что events не null
        if (result != null && result.getEvents() == null) {
            result.setEvents(new ArrayList<>());
            log.warn("CompilationDto {} имеет null events, устанавливаем пустой список", result.getId());
        }

        return result;
    }

    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        log.info("Создание подборки: {}", newCompilationDto);

        Compilation compilation = compilationMapper.toCompilation(newCompilationDto);

        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            List<Event> events = eventRepository.findByIdIn(newCompilationDto.getEvents());
            compilation.setEvents(events);
        }

        Compilation savedCompilation = compilationRepository.save(compilation);
        return compilationMapper.toCompilationDto(savedCompilation);
    }

    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        log.info("Обновление подборки {}: {}", compId, updateCompilationRequest);

        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка с ID " + compId + " не найдена"));

        // Обновляем только не-null поля
        if (updateCompilationRequest.getTitle() != null && !updateCompilationRequest.getTitle().trim().isEmpty()) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getEvents() != null) {
            List<Event> events = eventRepository.findByIdIn(updateCompilationRequest.getEvents());
            compilation.setEvents(events);
        }

        Compilation savedCompilation = compilationRepository.save(compilation);
        return compilationMapper.toCompilationDto(savedCompilation);
    }

    @Transactional
    public void deleteCompilation(Long compId) {
        log.info("Удаление подборки с ID: {}", compId);

        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Подборка с ID " + compId + " не найдена");
        }

        compilationRepository.deleteById(compId);
    }
}