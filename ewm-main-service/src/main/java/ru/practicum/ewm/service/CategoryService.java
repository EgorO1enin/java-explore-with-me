package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.request.NewCategoryDto;
import ru.practicum.ewm.dto.response.CategoryDto;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final EventRepository eventRepository;

    public List<CategoryDto> getCategories(int from, int size) {

        Pageable pageable = PageRequest.of(from / size, size);
        Page<Category> categories = categoryRepository.findAll(pageable);

        return categoryMapper.toCategoryDtoList(categories.getContent());
    }

    public CategoryDto getCategoryById(Long catId) {

        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с ID " + catId + " не найдена"));

        return categoryMapper.toCategoryDto(category);
    }

    @Transactional
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {

        if (categoryRepository.findByName(newCategoryDto.getName()).isPresent()) {
            throw new ConflictException("Категория с именем " + newCategoryDto.getName() + " уже существует");
        }

        Category category = categoryMapper.toCategory(newCategoryDto);
        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toCategoryDto(savedCategory);
    }

    @Transactional
    public CategoryDto updateCategory(Long catId, NewCategoryDto newCategoryDto) {

        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с ID " + catId + " не найдена"));

        if (categoryRepository.findByName(newCategoryDto.getName()).isPresent() &&
            !category.getName().equals(newCategoryDto.getName())) {
            throw new ConflictException("Категория с именем " + newCategoryDto.getName() + " уже существует");
        }

        category.setName(newCategoryDto.getName());
        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toCategoryDto(savedCategory);
    }

    @Transactional
    public void deleteCategory(Long catId) {

        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с ID " + catId + " не найдена"));

        if (eventRepository.countByCategoryId(catId) > 0) {
            throw new ConflictException("Нельзя удалить категорию с существующими событиями");
        }

        categoryRepository.delete(category);
    }

    public Category getCategoryEntityById(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с ID " + catId + " не найдена"));
    }
}
