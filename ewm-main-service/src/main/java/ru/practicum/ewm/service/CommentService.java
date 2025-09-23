package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.request.NewCommentDto;
import ru.practicum.ewm.dto.request.UpdateCommentAdminRequest;
import ru.practicum.ewm.dto.request.UpdateCommentRequest;
import ru.practicum.ewm.dto.response.CommentDto;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.enums.CommentStatus;
import ru.practicum.ewm.model.enums.EventState;
import ru.practicum.ewm.repository.CommentRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserService userService;
    private final EventService eventService;

    // Публичные методы

    /**
     * Получение комментариев к событию (только одобренные)
     */
    public Page<CommentDto> getCommentsByEvent(Long eventId, int from, int size) {
        log.info("Получение комментариев к событию с id={}, from={}, size={}", eventId, from, size);

        // Проверяем, что событие существует и опубликовано
        Event event = eventService.getEventEntityById(eventId);
        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Событие не найдено или не опубликовано");
        }

        Pageable pageable = PageRequest.of(from / size, size);
        Page<Comment> comments = commentRepository.findByEventIdAndStatus(eventId, CommentStatus.APPROVED, pageable);

        return comments.map(commentMapper::toDto);
    }

    // Приватные методы (для авторизованных пользователей)

    /**
     * Создание комментария
     */
    @Transactional
    public CommentDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        log.info("Создание комментария пользователем с id={} к событию с id={}", userId, eventId);

        // Дополнительная валидация текста
        if (newCommentDto.getText() == null || newCommentDto.getText().trim().isEmpty()) {
            throw new BadRequestException("Текст комментария не может быть пустым");
        }
        if (newCommentDto.getText().length() > 2000) {
            throw new BadRequestException("Текст комментария не может превышать 2000 символов");
        }

        // Проверяем, что пользователь существует
        User user = userService.getUserEntityById(userId);

        // Проверяем, что событие существует и опубликовано
        Event event = eventService.getEventEntityById(eventId);
        if (event.getState() != EventState.PUBLISHED) {
            throw new BadRequestException("Нельзя комментировать неопубликованное событие");
        }

        Comment comment = Comment.builder()
                .text(newCommentDto.getText())
                .author(user)
                .event(event)
                .created(LocalDateTime.now())
                .status(CommentStatus.PENDING)
                .build();

        Comment savedComment = commentRepository.save(comment);
        log.info("Комментарий создан с id={}", savedComment.getId());

        return commentMapper.toDto(savedComment);
    }

    /**
     * Получение комментариев пользователя
     */
    public Page<CommentDto> getUserComments(Long userId, int from, int size) {
        log.info("Получение комментариев пользователя с id={}, from={}, size={}", userId, from, size);

        // Проверяем, что пользователь существует
        userService.getUserEntityById(userId);

        Pageable pageable = PageRequest.of(from / size, size);
        Page<Comment> comments = commentRepository.findByAuthorId(userId, pageable);

        return comments.map(commentMapper::toDto);
    }

    /**
     * Обновление комментария пользователем
     */
    @Transactional
    public CommentDto updateComment(Long userId, Long commentId, UpdateCommentRequest updateCommentRequest) {
        log.info("Обновление комментария с id={} пользователем с id={}", commentId, userId);

        Comment comment = commentRepository.findByIdAndAuthorId(commentId, userId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));

        // Проверяем, что комментарий можно редактировать
        if (comment.getStatus() != CommentStatus.PENDING) {
            throw new ConflictException("Можно редактировать только комментарии в статусе ожидания модерации");
        }

        if (updateCommentRequest.getText() != null) {
            comment.setText(updateCommentRequest.getText());
        }

        comment.setUpdated(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);

        log.info("Комментарий обновлен с id={}", savedComment.getId());
        return commentMapper.toDto(savedComment);
    }

    /**
     * Удаление комментария пользователем
     */
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        log.info("Удаление комментария с id={} пользователем с id={}", commentId, userId);

        Comment comment = commentRepository.findByIdAndAuthorId(commentId, userId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));

        // Проверяем, что комментарий можно удалить
        if (comment.getStatus() != CommentStatus.PENDING) {
            throw new ConflictException("Можно удалять только комментарии в статусе ожидания модерации");
        }

        commentRepository.delete(comment);
        log.info("Комментарий удален с id={}", commentId);
    }

    // Административные методы

    /**
     * Получение комментариев для модерации
     */
    public Page<CommentDto> getCommentsForModeration(int from, int size) {
        log.info("Получение комментариев для модерации, from={}, size={}", from, size);

        Pageable pageable = PageRequest.of(from / size, size);
        Page<Comment> comments = commentRepository.findByStatus(CommentStatus.PENDING, pageable);

        return comments.map(commentMapper::toDto);
    }

    /**
     * Модерация комментария администратором
     */
    @Transactional
    public CommentDto moderateComment(Long commentId, UpdateCommentAdminRequest updateCommentAdminRequest) {
        log.info("Модерация комментария с id={}, статус={}", commentId, updateCommentAdminRequest.getStatus());

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));

        if (comment.getStatus() != CommentStatus.PENDING) {
            throw new ConflictException("Можно модерировать только комментарии в статусе ожидания");
        }

        CommentStatus newStatus;
        try {
            newStatus = CommentStatus.valueOf(updateCommentAdminRequest.getStatus());
            if (newStatus == CommentStatus.PENDING) {
                throw new BadRequestException("Нельзя установить статус PENDING");
            }
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Некорректный статус: " + updateCommentAdminRequest.getStatus());
        }

        comment.setStatus(newStatus);
        comment.setUpdated(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);

        log.info("Комментарий с id={} получил статус {}", commentId, newStatus);
        return commentMapper.toDto(savedComment);
    }

    /**
     * Удаление комментария администратором
     */
    @Transactional
    public void deleteCommentByAdmin(Long commentId) {
        log.info("Удаление комментария с id={} администратором", commentId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));

        commentRepository.delete(comment);
        log.info("Комментарий удален администратором с id={}", commentId);
    }

    /**
     * Поиск комментариев по тексту
     */
    public Page<CommentDto> searchComments(String text, int from, int size) {
        log.info("Поиск комментариев по тексту: {}, from={}, size={}", text, from, size);

        Pageable pageable = PageRequest.of(from / size, size);
        Page<Comment> comments = commentRepository.findByTextContainingIgnoreCaseAndStatus(
                text, CommentStatus.APPROVED, pageable);

        return comments.map(commentMapper::toDto);
    }
}
