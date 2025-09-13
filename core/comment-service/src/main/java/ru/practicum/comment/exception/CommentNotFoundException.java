package ru.practicum.comment.exception;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(Long commentId) {

        super("Comment with id=" + commentId + " was not found");
    }
}
