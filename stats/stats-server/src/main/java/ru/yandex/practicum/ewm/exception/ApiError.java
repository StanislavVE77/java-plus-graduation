package ru.yandex.practicum.ewm.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiError {
    private HttpStatus status;
    private String userMessage;
    private String exceptionMessage;
    private String stackTrace;

    public ApiError(HttpStatus status, String userMessage, String exceptionMessage, String stackTrace) {
        this.status = status;
        this.userMessage = userMessage;
        this.exceptionMessage = exceptionMessage;
        this.stackTrace = stackTrace;
    }

}
