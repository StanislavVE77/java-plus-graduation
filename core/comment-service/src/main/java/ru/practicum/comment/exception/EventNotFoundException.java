package ru.practicum.comment.exception;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(Long eventId) {
        super("Event with id=" + eventId + " was not found");
    }
}
