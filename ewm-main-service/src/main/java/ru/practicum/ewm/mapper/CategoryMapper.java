package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.dto.request.NewCategoryDto;
import ru.practicum.ewm.dto.response.CategoryDto;
import ru.practicum.ewm.model.Category;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    
    CategoryDto toCategoryDto(Category category);
    
    Category toCategory(NewCategoryDto newCategoryDto);
    
    List<CategoryDto> toCategoryDtoList(List<Category> categories);
}
