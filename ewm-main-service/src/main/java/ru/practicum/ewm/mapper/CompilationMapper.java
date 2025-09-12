package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.practicum.ewm.dto.request.NewCompilationDto;
import ru.practicum.ewm.dto.request.UpdateCompilationRequest;
import ru.practicum.ewm.dto.response.CompilationDto;
import ru.practicum.ewm.model.Compilation;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompilationMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", ignore = true)
    Compilation toCompilation(NewCompilationDto newCompilationDto);
    
    @Mapping(target = "events", ignore = true)
    void updateCompilation(UpdateCompilationRequest updateCompilationRequest, @MappingTarget Compilation compilation);
    
    CompilationDto toCompilationDto(Compilation compilation);
    
    List<CompilationDto> toCompilationDtoList(List<Compilation> compilations);
}
