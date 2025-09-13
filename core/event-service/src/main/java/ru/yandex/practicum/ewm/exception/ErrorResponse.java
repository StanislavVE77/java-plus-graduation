package ru.yandex.practicum.ewm.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String status;
    private String reason;
    private String message;
    private LocalDateTime timestamp;
}
