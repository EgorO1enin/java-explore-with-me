package ru.practicum.ewm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.request.NewCategoryDto;
import ru.practicum.ewm.dto.response.CategoryDto;
import ru.practicum.ewm.service.CategoryService;
import ru.practicum.ewm.service.StatsService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin: Категории", description = "API для работы с категориями администратора")
public class AdminCategoryController {
    
    private final CategoryService categoryService;
    private final StatsService statsService;
    
    @PostMapping
    @Operation(summary = "Добавление новой категории")
    public ResponseEntity<CategoryDto> createCategory(
            @Valid @RequestBody NewCategoryDto newCategoryDto,
            HttpServletRequest request) {
        log.info("POST /admin/categories - создание категории администратором");
        statsService.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
        
        CategoryDto category = categoryService.createCategory(newCategoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }
    
    @PatchMapping("/{catId}")
    @Operation(summary = "Изменение категории")
    public ResponseEntity<CategoryDto> updateCategory(
            @Parameter(description = "ID категории") @PathVariable Long catId,
            @Valid @RequestBody NewCategoryDto newCategoryDto,
            HttpServletRequest request) {
        log.info("PATCH /admin/categories/{} - обновление категории администратором", catId);
        statsService.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
        
        CategoryDto category = categoryService.updateCategory(catId, newCategoryDto);
        return ResponseEntity.ok(category);
    }
    
    @DeleteMapping("/{catId}")
    @Operation(summary = "Удаление категории")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "ID категории") @PathVariable Long catId,
            HttpServletRequest request) {
        log.info("DELETE /admin/categories/{} - удаление категории администратором", catId);
        statsService.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
        
        categoryService.deleteCategory(catId);
        return ResponseEntity.noContent().build();
    }
}
