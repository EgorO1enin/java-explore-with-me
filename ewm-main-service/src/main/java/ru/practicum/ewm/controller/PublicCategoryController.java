package ru.practicum.ewm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.response.CategoryDto;
import ru.practicum.ewm.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Public: Категории", description = "Публичный API для работы с категориями")
public class PublicCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Получение категорий")
    public ResponseEntity<List<CategoryDto>> getCategories(
            @Parameter(description = "количество категорий, которые нужно пропустить")
            @RequestParam(defaultValue = "0") int from,
            @Parameter(description = "количество категорий в наборе")
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /categories - получение категорий: from={}, size={}", from, size);
        List<CategoryDto> categories = categoryService.getCategories(from, size);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{catId}")
    @Operation(summary = "Получение информации о категории по её идентификатору")
    public ResponseEntity<CategoryDto> getCategory(
            @Parameter(description = "id категории") @PathVariable Long catId) {
        log.info("GET /categories/{} - получение категории", catId);
        CategoryDto categoryDto = categoryService.getCategoryById(catId);
        return ResponseEntity.ok(categoryDto);
    }
}
