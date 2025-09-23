package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.enums.CommentStatus;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByEventIdAndStatus(Long eventId, CommentStatus status, Pageable pageable);

    Page<Comment> findByAuthorId(Long authorId, Pageable pageable);

    Optional<Comment> findByIdAndAuthorId(Long commentId, Long authorId);

    Page<Comment> findByStatus(CommentStatus status, Pageable pageable);

    Page<Comment> findByTextContainingIgnoreCaseAndStatus(String text, CommentStatus status, Pageable pageable);
}
