package ru.practicum.request.service;

import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.model.Request;

import java.util.List;
import java.util.Set;

public interface RequestService {
    RequestDto create(Long userId, Long eventId);

    List<RequestDto> get(Long userId);

    RequestDto update(Long userId, Long requestId);

    Request updateInternal(Request request);

    List<Request> getByEventId(Long eventId);

    List<Request> getByEventIdAndIds(Long eventId, Set<Long> requestIds);
}
