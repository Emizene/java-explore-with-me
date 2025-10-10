package ru.practicum.service.comment;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.NewCommentDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CommentDto createComment(Long eventId, Long userId, NewCommentDto comment) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found with id: " + eventId));

        Comment entity = commentMapper.toEntity(comment, event);
        entity.setText(comment.getText());
        entity.setCreated(LocalDateTime.now());
        entity.setAuthor(user);

        commentRepository.save(entity);
        log.info("Успешное добавление комментария пользователя с ID={} к событию с ID={}", userId, eventId);

        return commentMapper.toDto(entity);
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long eventId, Long commentId, Long userId, NewCommentDto newCommentDto) {
        Comment comment = commentRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Comment not found with id: " + commentId));

        checkEventExists(eventId);
        checkUserExists(userId);
        checkIfUserIsAuthor(userId, comment);

        comment.setText(newCommentDto.getText());

        commentRepository.save(comment);

        return commentMapper.toDto(comment);
    }

    @Override
    public List<CommentDto> getAllComments(Long eventId) {
        checkEventExists(eventId);

        return commentRepository.findAllByEvent_IdOrderByCreatedAsc(eventId).stream()
                .map(commentMapper::toDto)
                .toList();
    }

    @Override
    public CommentDto getCommentById(Long eventId, Long commentId) {
        checkEventExists(eventId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found with id: " + commentId));

        return commentMapper.toDto(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId, Long eventId) {
        checkEventExists(eventId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found with id: " + commentId));

        checkIfUserIsAuthor(userId, comment);
        if (!commentRepository.existsById(commentId)) {
            throw new NotFoundException("Comment not found with id: " + commentId);
        }
        commentRepository.deleteById(commentId);
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found with id: " + userId);
        }
    }

    private void checkEventExists(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Event not found with id: " + eventId);
        }
    }

    private void checkIfUserIsAuthor(Long userId, Comment comment) {
        if (!(Objects.equals(comment.getAuthor().getId(), userId))) {
            throw new ConflictException("Only authors can update comment");
        }
    }
}
