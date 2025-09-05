package ru.yandex.practicum.ewm.request.service;

import ru.yandex.practicum.ewm.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto create(Long userId, Long eventId);

    List<RequestDto> get(Long userId);

    RequestDto update(Long userId, Long requestId);
}
