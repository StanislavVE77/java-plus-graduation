package ru.practicum.comment.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userId) {
        super("User with id=" + userId + " was not found");
    }
}
