package ru.yandex.practicum.ewm.exception;

public class EventGetBadRequestException extends RuntimeException {
    public EventGetBadRequestException(Long eventId, Long userId) {
        super("Event with id=" + eventId + " does not belong to user with id = " + userId);
    }

}
