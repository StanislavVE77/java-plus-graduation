package ru.yandex.practicum.ewm.exception;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(Long eventId) {
        super("Event with id=" + eventId + " was not found");
    }
}
