package ru.yandex.practicum.ewm.request.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Request {
    private Long id;

    private LocalDateTime created;

    private Long requesterId;

    private Long eventId;

    private RequestStatus status;
}
