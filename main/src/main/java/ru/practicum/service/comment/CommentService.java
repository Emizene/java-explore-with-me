package ru.practicum.service.comment;

import ru.practicum.dto.CommentDto;
import ru.practicum.dto.NewCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto createComment(Long eventId, Long userId, NewCommentDto comment);

    CommentDto updateComment(Long eventId, Long commentId, Long userId, NewCommentDto newCommentDto);

    void deleteComment(Long userId, Long commentId, Long eventId);

    CommentDto getCommentById(Long eventId, Long commentId);

    List<CommentDto> getAllComments(Long eventId);
}
