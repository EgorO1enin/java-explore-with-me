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
import ru.practicum.ewm.dto.request.NewUserRequest;
import ru.practicum.ewm.dto.response.UserDto;
import ru.practicum.ewm.service.StatsService;
import ru.practicum.ewm.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin: Пользователи", description = "API для работы с пользователями администратора")
public class AdminUserController {
    
    private final UserService userService;
    private final StatsService statsService;
    
    @GetMapping
    @Operation(summary = "Получение информации о пользователях")
    public ResponseEntity<List<UserDto>> getUsers(
            @Parameter(description = "Список id пользователей для которых нужно получить информацию") 
            @RequestParam(required = false) List<Long> ids,
            @Parameter(description = "Количество элементов, которые нужно пропустить для формирования текущего набора") 
            @RequestParam(defaultValue = "0") int from,
            @Parameter(description = "Количество элементов в наборе") 
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        log.info("GET /admin/users - получение пользователей администратором");
        statsService.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
        
        List<UserDto> users = userService.getUsers(ids, from, size);
        return ResponseEntity.ok(users);
    }
    
    @PostMapping
    @Operation(summary = "Добавление нового пользователя")
    public ResponseEntity<UserDto> createUser(
            @Valid @RequestBody NewUserRequest newUserRequest,
            HttpServletRequest request) {
        log.info("POST /admin/users - создание пользователя администратором");
        statsService.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
        
        UserDto user = userService.createUser(newUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
    
    @DeleteMapping("/{userId}")
    @Operation(summary = "Удаление пользователя")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID пользователя") @PathVariable Long userId,
            HttpServletRequest request) {
        log.info("DELETE /admin/users/{} - удаление пользователя администратором", userId);
        statsService.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
        
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
