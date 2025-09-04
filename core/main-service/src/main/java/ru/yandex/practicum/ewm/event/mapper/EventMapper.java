package ru.yandex.practicum.ewm.event.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.ewm.category.model.Category;
import ru.yandex.practicum.ewm.event.dto.*;
import ru.yandex.practicum.ewm.event.model.*;
import ru.yandex.practicum.ewm.user.model.User;
import ru.yandex.practicum.ewm.user.dto.UserShortDto;

@Component
@RequiredArgsConstructor
public class EventMapper {

    public EventFullDto toEventFullDto(Event event) {
        EventFullDto eventFullDto = new EventFullDto();
        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setCategory(new Category(event.getCategory().getId(), event.getCategory().getName()));
        eventFullDto.setConfirmedRequests(event.getConfirmedRequests());
        eventFullDto.setCreatedOn(event.getCreatedOn());
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setEventDate(event.getEventDate());
        eventFullDto.setId(event.getId());
        eventFullDto.setInitiator(new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()));
        eventFullDto.setLocation(new Location(event.getLat(), event.getLon()));
        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setPublishedOn(event.getPublishedOn());
        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setState(event.getState());
        eventFullDto.setTitle(event.getTitle());
        eventFullDto.setViews(event.getViews());

        return eventFullDto;
    }

    public EventShortDto toEventShortDto(Event event) {
        EventShortDto eventShortDto = new EventShortDto();
        eventShortDto.setAnnotation(event.getAnnotation());
        eventShortDto.setCategory(new Category(event.getCategory().getId(), event.getCategory().getName()));
        eventShortDto.setConfirmedRequests(event.getConfirmedRequests());
        eventShortDto.setEventDate(event.getEventDate());
        eventShortDto.setId(event.getId());
        eventShortDto.setInitiator(new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()));
        eventShortDto.setPaid(event.getPaid());
        eventShortDto.setTitle(event.getTitle());
        eventShortDto.setViews(event.getViews());

        return eventShortDto;
    }

    public Event toEventFromUpdateAdmin(EventUpdateAdminDto eventDto, Category updCategory, Event curEvent) {

        if (eventDto.getAnnotation() != null) {
            curEvent.setAnnotation(eventDto.getAnnotation());
        }
        curEvent.setCategory(updCategory);
        if (eventDto.getDescription() != null) {
            curEvent.setDescription(eventDto.getDescription());
        }
        if (eventDto.getEventDate() != null) {
            curEvent.setEventDate(eventDto.getEventDate());
        }
        if (eventDto.getLocation() != null) {
            curEvent.setLat(eventDto.getLocation().lat);
            curEvent.setLon(eventDto.getLocation().lon);
        }
        if (eventDto.getPaid() != null) {
            curEvent.setPaid(eventDto.getPaid());
        }
        if (eventDto.getParticipantLimit() != null) {
            curEvent.setParticipantLimit(eventDto.getParticipantLimit());
        }
        if (eventDto.getStateAction() != null && eventDto.getStateAction().equals(EventStateAction.PUBLISH_EVENT)) {
            curEvent.setState(EventState.PUBLISHED);
        }
        if (eventDto.getStateAction() != null && eventDto.getStateAction().equals(EventStateAction.REJECT_EVENT)) {
            curEvent.setState(EventState.CANCELED);
        }
        if (eventDto.getTitle() != null) {
            curEvent.setTitle(eventDto.getTitle());
        }

        return curEvent;
    }

    public Event toEventFromUpdateUser(EventUpdateUserDto eventDto, Category updCategory, Event curEvent) {

        if (eventDto.getAnnotation() != null) {
            curEvent.setAnnotation(eventDto.getAnnotation());
        }
        curEvent.setCategory(updCategory);
        if (eventDto.getDescription() != null) {
            curEvent.setDescription(eventDto.getDescription());
        }
        if (eventDto.getEventDate() != null) {
            curEvent.setEventDate(eventDto.getEventDate());
        }
        if (eventDto.getLocation() != null) {
            curEvent.setLat(eventDto.getLocation().lat);
            curEvent.setLon(eventDto.getLocation().lon);
        }
        if (eventDto.getParticipantLimit() != null) {
            curEvent.setParticipantLimit(eventDto.getParticipantLimit());
        }
        if (eventDto.getStateAction() != null && eventDto.getStateAction().equals(EventUserStateAction.SEND_TO_REVIEW)) {
            curEvent.setState(EventState.PENDING);
        }
        if (eventDto.getStateAction() != null && eventDto.getStateAction().equals(EventUserStateAction.CANCEL_REVIEW)) {
            curEvent.setState(EventState.CANCELED);
        }
        if (eventDto.getTitle() != null) {
            curEvent.setTitle(eventDto.getTitle());
        }

        return curEvent;
    }

    public Event toEventFromCreatedDto(EventCreateDto eventDto, User user, Category category) {
        Event event = new Event();
        event.setAnnotation(eventDto.getAnnotation());
        event.setCategory(category);
        event.setDescription(eventDto.getDescription());
        event.setCreatedOn(eventDto.getCreated());
        event.setEventDate(eventDto.getEventDate());
        event.setLat(eventDto.getLocation().lat);
        event.setLon(eventDto.getLocation().lon);
        event.setPaid(eventDto.getPaid());
        event.setParticipantLimit(eventDto.getParticipantLimit());
        event.setRequestModeration(eventDto.getRequestModeration());
        event.setInitiator(user);
        event.setTitle(eventDto.getTitle());
        event.setConfirmedRequests(0);
        event.setState(EventState.PENDING);
        event.setViews(eventDto.getViews());

        return event;
    }

}
