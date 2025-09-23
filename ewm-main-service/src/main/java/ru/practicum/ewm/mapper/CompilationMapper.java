package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.ewm.dto.request.NewCompilationDto;
import ru.practicum.ewm.dto.request.UpdateCompilationRequest;
import ru.practicum.ewm.dto.response.CompilationDto;
import ru.practicum.ewm.dto.response.EventShortDto;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompilationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", ignore = true)
    Compilation toCompilation(NewCompilationDto newCompilationDto);

    @Mapping(target = "events", ignore = true)
    void updateCompilation(UpdateCompilationRequest updateCompilationRequest, @MappingTarget Compilation compilation);

    @Mapping(target = "events", expression = "java(mapEvents(compilation.getEvents()))")
    CompilationDto toCompilationDto(Compilation compilation);

    List<CompilationDto> toCompilationDtoList(List<Compilation> compilations);

    default List<EventShortDto> mapEvents(List<Event> events) {
        if (events == null) {
            return Collections.emptyList();
        }
        return events.stream()
                .map(this::eventToEventShortDto)
                .toList();
    }

    EventShortDto eventToEventShortDto(Event event);
}
