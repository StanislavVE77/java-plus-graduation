package ru.yandex.practicum.ewm.request.mapper;

import ru.yandex.practicum.ewm.request.dto.RequestDto;
import ru.yandex.practicum.ewm.request.dto.RequestEventDto;
import ru.yandex.practicum.ewm.user.dto.UserRequestDto;
import ru.yandex.practicum.ewm.event.model.Event;
import ru.yandex.practicum.ewm.request.model.Request;
import ru.yandex.practicum.ewm.request.model.RequestStatus;

import java.time.LocalDateTime;

public class RequestMapper {
    public static Request toEntity(UserRequestDto requester, Event event, RequestStatus status) {
        Request request = new Request();
        request.setEventId(event.getId());
        request.setRequesterId(requester.getId());
        request.setCreated(LocalDateTime.now());
        request.setStatus(status);

        return request;
    }

    public static RequestDto toDto(Request request) {
        return new RequestDto(
                request.getId(),
                request.getCreated(),
                request.getRequesterId(),
                request.getEventId(),
                request.getStatus()
        );
    }

    public static RequestEventDto toEventRequestDto(Request request) {
        return new RequestEventDto(
                request.getId(),
                request.getCreated(),
                request.getRequesterId(),
                request.getEventId(),
                request.getStatus()
        );
    }

    public static RequestEventDto toEventRequestDtoInt(RequestDto request) {
        return new RequestEventDto(
                request.getId(),
                request.getCreated(),
                request.getRequester(),
                request.getEvent(),
                request.getStatus()
        );
    }

}
