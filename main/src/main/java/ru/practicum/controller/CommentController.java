package ru.practicum.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.NewCommentDto;
import ru.practicum.service.comment.CommentService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/event/{eventId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentDto> createComment(@PathVariable("eventId") Long eventId,
                                                    @RequestHeader("X-User-Id") Long userId,
                                                    @Valid @RequestBody NewCommentDto newCommentDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(eventId, userId, newCommentDto));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable("eventId") Long eventId,
                                                    @PathVariable("commentId") Long commentId,
                                                    @RequestHeader("X-User-Id") Long userId,
                                                    @Valid @RequestBody NewCommentDto newCommentDto) {
        return ResponseEntity.ok(commentService.updateComment(eventId, commentId, userId, newCommentDto));
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getAllComments(@PathVariable("eventId") Long eventId) {
        return ResponseEntity.ok(commentService.getAllComments(eventId));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDto> getComment(@PathVariable("eventId") Long eventId,
                                                 @PathVariable("commentId") Long commentId) {
        return ResponseEntity.ok(commentService.getCommentById(eventId, commentId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@RequestHeader("X-User-Id") Long userId, @PathVariable("commentId") Long commentId, @PathVariable Long eventId) {
        commentService.deleteComment(userId, commentId, eventId);
        return ResponseEntity.noContent().build();
    }
}
