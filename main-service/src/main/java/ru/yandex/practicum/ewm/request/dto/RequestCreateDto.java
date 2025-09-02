package ru.yandex.practicum.ewm.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.ewm.request.model.RequestStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RequestCreateDto {
    private LocalDateTime created;

    private Long requesterId;

    private Long eventId;

    private RequestStatus status;
}
