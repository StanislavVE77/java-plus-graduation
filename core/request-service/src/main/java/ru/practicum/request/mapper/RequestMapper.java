package ru.practicum.request.mapper;

import ru.practicum.request.model.Event;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestEventDto;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.dto.UserRequestDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RequestMapper {
    public static Request toEntity(UserRequestDto requester, Event event, RequestStatus status) {

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        Request request = new Request();
        request.setEventId(event.getId());
        request.setRequesterId(requester.getId());
        request.setCreated(LocalDateTime.parse(formattedDateTime));
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

    public static Request toRequest(RequestDto requestDto) {
        return new Request(
            requestDto.getId(),
            requestDto.getCreated(),
            requestDto.getRequester(),
            requestDto.getEvent(),
            requestDto.getStatus()
        );
    }

}
