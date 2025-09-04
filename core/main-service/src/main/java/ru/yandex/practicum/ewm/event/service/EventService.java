package ru.yandex.practicum.ewm.event.service;

import ru.yandex.practicum.ewm.event.dto.*;
import ru.yandex.practicum.ewm.event.model.AdminEventParams;
import ru.yandex.practicum.ewm.event.model.PrivateEventParams;
import ru.yandex.practicum.ewm.event.model.PublicEventParams;
import ru.yandex.practicum.ewm.request.dto.RequestEventDto;

import java.util.List;

public interface EventService {
        List<EventFullDto> getAdmin(AdminEventParams params);

        List<EventShortDto> getPublic(PublicEventParams params);

        List<EventShortDto> getPrivate(PrivateEventParams params);

        EventFullDto getByIdPublic(Long eventId, PublicEventParams params);

        EventFullDto getByIdPrivate(Long userId, Long eventId);

        EventFullDto update(Long eventId, EventUpdateAdminDto eventDto);

        EventFullDto updatePrivate(Long userId, Long eventId, EventUpdateUserDto eventUpdateDto);

        EventFullDto create(Long userId, EventCreateDto eventDto);

        List<RequestEventDto> getRequestsByIdPrivate(Long userId, Long eventId);

        EventResultRequestStatusDto updateRequestStatusPrivate(Long userId, Long eventId, EventUpdateRequestStatusDto updateDto);
}
