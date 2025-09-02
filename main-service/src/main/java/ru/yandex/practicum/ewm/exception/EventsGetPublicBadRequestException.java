package ru.yandex.practicum.ewm.exception;

public class EventsGetPublicBadRequestException extends RuntimeException {
    public EventsGetPublicBadRequestException() {
        super("Events not found.");
    }
}
