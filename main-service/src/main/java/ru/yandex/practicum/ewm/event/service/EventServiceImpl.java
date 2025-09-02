package ru.yandex.practicum.ewm.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.yandex.practicum.ewm.category.model.Category;
import ru.yandex.practicum.ewm.category.model.QCategory;
import ru.yandex.practicum.ewm.category.storage.CategoryRepository;
import ru.yandex.practicum.ewm.event.dto.*;
import ru.yandex.practicum.ewm.event.mapper.EventMapper;
import ru.yandex.practicum.ewm.event.model.*;
import ru.yandex.practicum.ewm.event.storage.EventRepository;
import ru.yandex.practicum.ewm.exception.*;
import ru.practicum.client.StatRestClient;
import ru.yandex.practicum.ewm.request.dto.RequestEventDto;
import ru.yandex.practicum.ewm.request.mapper.RequestMapper;
import ru.yandex.practicum.ewm.request.model.Request;
import ru.yandex.practicum.ewm.request.model.RequestStatus;
import ru.yandex.practicum.ewm.request.storage.RequestRepository;
import ru.yandex.practicum.ewm.user.model.User;
import ru.yandex.practicum.ewm.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@AllArgsConstructor
@ComponentScan(value = {"ru.yandex.practicum.ewm", "ru.practicum.client"})
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final EventMapper mapper;

    StatRestClient statClient;

    @Override
    public List<EventFullDto> getAdmin(AdminEventParams params) {

        PageRequest pageRequest = PageRequest.of(params.getFrom() > 0 ? params.getFrom() / params.getSize() : 0, params.getSize());

        BooleanExpression filter = byStates(params.getStates())
                .and(byCategoryIds(params.getCategories()))
                .and(byUserIds(params.getUsers()))
                .and(byDates(params.getRangeStart(), params.getRangeEnd()));

        Page<Event> pageEvents = eventRepository.findAll(filter, pageRequest);
        List<Event> foundEvents = pageEvents.getContent();

        return foundEvents.stream()
                .map(mapper::toEventFullDto)
                .toList();
    }

    @Override
    public List<EventShortDto> getPublic(PublicEventParams params) {

        PageRequest pageRequest;
        if (params.getSort() != null) {
            if (params.getSort().equals(EventPublicSort.EVENT_DATE)) {
                pageRequest = PageRequest.of(params.getFrom() > 0 ? params.getFrom() / params.getSize() : 0, params.getSize(), Sort.by("eventDate"));
            } else if (params.getSort().equals(EventPublicSort.VIEWS)) {
                pageRequest = PageRequest.of(params.getFrom() > 0 ? params.getFrom() / params.getSize() : 0, params.getSize(), Sort.by("views"));
            } else {
                pageRequest = PageRequest.of(params.getFrom() > 0 ? params.getFrom() / params.getSize() : 0, params.getSize());
            }
        } else {
            pageRequest = PageRequest.of(params.getFrom() > 0 ? params.getFrom() / params.getSize() : 0, params.getSize());
        }

        BooleanExpression filter = byPublishedEvents()
                .and(byText(params.getText()))
                .and(byCategoryIds(params.getCategories()))
                .and(byPaid(params.getPaid()))
                .and(byOnlyAvailable(params.getOnlyAvailable()))
                .and(byDatesWithDefaults(params.getRangeStart(), params.getRangeEnd()));

        Page<Event> pageEvents = eventRepository.findAll(filter, pageRequest);
        List<Event> foundEvents = pageEvents.getContent();
        if (foundEvents.isEmpty()) {
            throw new EventsGetPublicBadRequestException();
        }
        statClient.saveHit(new HitDto("ewm-main-service", "/events", params.getIpAdr(), LocalDateTime.now()));


        return foundEvents.stream()
                .map(mapper::toEventShortDto)
                .toList();
    }

    @Override
    public List<EventShortDto> getPrivate(PrivateEventParams params) {
        Optional<User> user = userRepository.findById(params.getUserId());
        if (user.isEmpty()) {
            throw new UserNotFoundException(params.getUserId());
        }

        PageRequest pageRequest = PageRequest.of(params.getFrom() > 0 ? params.getFrom() / params.getSize() : 0, params.getSize());
        BooleanExpression filter = byUserIds(Set.of(params.getUserId()));
        Page<Event> pageEvents = eventRepository.findAll(filter, pageRequest);
        List<Event> foundEvents = pageEvents.getContent();

        return foundEvents.stream()
                .map(mapper::toEventShortDto)
                .toList();
    }

    @Override
    public EventFullDto getByIdPublic(Long eventId, PublicEventParams params) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty() || !event.get().getState().equals(EventState.PUBLISHED)) {
            throw new EventNotFoundException(eventId);
        }
        List<StatsDto> stats = statClient.getStats("1900-01-01 00:00:00", "2100-01-01 00:00:00", List.of("/events/" + eventId), true);
        if (stats.isEmpty()) {
            event.get().setViews(event.get().getViews() + 1);
            eventRepository.save(event.get());
        }
        statClient.saveHit(new HitDto("ewm-main-service", "/events/" + eventId, params.getIpAdr(), LocalDateTime.now()));
        return mapper.toEventFullDto(event.get());
    }

    @Override
    public EventFullDto getByIdPrivate(Long userId, Long eventId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            throw new EventNotFoundException(eventId);
        }
        if (!Objects.equals(event.get().getInitiator().getId(), userId)) {
            throw new EventGetBadRequestException(eventId, userId);
        }
        return mapper.toEventFullDto(event.get());
    }

    @Override
    public EventFullDto update(Long eventId, EventUpdateAdminDto eventDto) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            throw new EventNotFoundException(eventId);
        }
        Optional<Category> category;
        if (eventDto.getCategory() != null && !eventDto.getCategory().equals(event.get().getCategory().getId())) {
            category = categoryRepository.findById(eventDto.getCategory());
            if (category.isEmpty()) {
                throw new CategoryNotFoundException(eventDto.getCategory());
            }
        } else {
            category = Optional.of(event.get().getCategory());
        }
        if (eventDto.getEventDate() != null && eventDto.getEventDate().isBefore(LocalDateTime.now())) {
            throw new EventDateException("Изменение даты события не может быть на уже наступившую.");
        }
        if (eventDto.getEventDate() != null && event.get().getPublishedOn() != null && eventDto.getEventDate().isBefore(event.get().getPublishedOn().minus(1, ChronoUnit.HOURS))) {
            throw new DataIntegrityViolationException("Дата начала изменяемого события должна быть не ранее чем за час от даты публикации.");
        }
        if (eventDto.getStateAction() != null && eventDto.getStateAction().equals(EventStateAction.PUBLISH_EVENT) && !event.get().getState().equals(EventState.PENDING)) {
            throw new DataIntegrityViolationException("Событие можно публиковать, только если оно в состоянии ожидания публикации");
        }
        if (eventDto.getStateAction() != null && eventDto.getStateAction().equals(EventStateAction.REJECT_EVENT) && event.get().getState().equals(EventState.PUBLISHED)) {
            throw new DataIntegrityViolationException("Событие можно отклонить, только если оно еще не опубликовано");
        }
        Event updEvent = mapper.toEventFromUpdateAdmin(eventDto, category.get(), event.get());
        updEvent = eventRepository.save(updEvent);
        return mapper.toEventFullDto(updEvent);
    }

    @Override
    public EventFullDto updatePrivate(Long userId, Long eventId, EventUpdateUserDto eventDto) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            throw new EventNotFoundException(eventId);
        }
        Optional<Category> category;
        if (eventDto.getCategory() != null && !eventDto.getCategory().equals(event.get().getCategory().getId())) {
            category = categoryRepository.findById(eventDto.getCategory());
            if (category.isEmpty()) {
                throw new CategoryNotFoundException(eventDto.getCategory());
            }
        } else {
            category = Optional.of(event.get().getCategory());
        }
        if (!Objects.equals(event.get().getInitiator().getId(), userId)) {
            throw new EventGetBadRequestException(eventId, userId);
        }
        if (eventDto.getEventDate() != null && eventDto.getEventDate().minusHours(2).isBefore(LocalDateTime.now())) {
            throw new EventDateException("Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента.");
        }
        if (event.get().getState().equals(EventState.PUBLISHED)) {
            throw new DataIntegrityViolationException("Изменить можно только отмененные события или события в состоянии ожидания модерации.");
        }
        Event updEvent = mapper.toEventFromUpdateUser(eventDto, category.get(), event.get());
        updEvent = eventRepository.save(updEvent);
        return mapper.toEventFullDto(updEvent);
    }

    @Override
    public EventFullDto create(Long userId, EventCreateDto eventDto) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        Optional<Category> category = categoryRepository.findById(eventDto.getCategory());
        if (category.isEmpty()) {
            throw new CategoryNotFoundException(eventDto.getCategory());
        }
        Event event = mapper.toEventFromCreatedDto(eventDto, user.get(), category.get());
        event = eventRepository.save(event);
        return mapper.toEventFullDto(event);
    }

    @Override
    public List<RequestEventDto> getRequestsByIdPrivate(Long userId, Long eventId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            throw new EventNotFoundException(eventId);
        }

        if (!event.get().getInitiator().getId().equals(userId)) {
            throw new ConflictException("Вы не являетесь владельцем данного события");
        }
        List<Request> requests = requestRepository.findAllByEventId(eventId);
        return requests.stream()
                .map(RequestMapper::toEventRequestDto)
                .toList();
    }

    @Override
    public EventResultRequestStatusDto updateRequestStatusPrivate(Long userId, Long eventId, EventUpdateRequestStatusDto updateDto) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            throw new EventNotFoundException(eventId);
        }
        Integer confReqs = event.get().getConfirmedRequests();
        Integer limit = event.get().getParticipantLimit();
        if (limit == 0 || !event.get().getRequestModeration()) {
            return null;
        }
        if (Objects.equals(confReqs, limit) && updateDto.getStatus().equals(RequestStatus.CONFIRMED)) {
            throw new DataIntegrityViolationException("The participant limit has been reached");
        }
        int count = limit - confReqs;
        int counter = 0;
        List<Request> requests = requestRepository.findAllByEventIdAndIdIn(eventId, updateDto.getRequestIds());
        List<RequestEventDto> confirmedRequests = new ArrayList<>();
        List<RequestEventDto> rejectedRequests = new ArrayList<>();
        for (Request request : requests) {
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new DataIntegrityViolationException("Request must have status PENDING");
            }
            if (updateDto.getStatus().equals(RequestStatus.CONFIRMED) && counter < count) {
                counter++;
                request.setStatus(RequestStatus.CONFIRMED);
                requestRepository.save(request);
                RequestEventDto requestDto = RequestMapper.toEventRequestDto(request);
                confirmedRequests.add(requestDto);
            } else {
                counter++;
                request.setStatus(RequestStatus.REJECTED);
                requestRepository.save(request);
                RequestEventDto requestDto = RequestMapper.toEventRequestDto(request);
                rejectedRequests.add(requestDto);
            }
        }
        event.get().setConfirmedRequests(confReqs + counter);
        eventRepository.save(event.get());

        EventResultRequestStatusDto results = new EventResultRequestStatusDto();
        results.setConfirmedRequests(confirmedRequests);
        results.setRejectedRequests(rejectedRequests);
        return results;
    }

    private BooleanExpression byStates(Set<EventState> states) {

        return states != null ? QEvent.event.state.in(states) : QEvent.event.state.in(Set.of(EventState.CANCELED, EventState.PENDING, EventState.PUBLISHED));
    }

    private BooleanExpression byCategoryIds(Set<Long> categories) {
        return categories != null && !categories.isEmpty() && categories.iterator().next() != 0 ? QCategory.category.id.in(categories) : null;
    }

    private BooleanExpression byUserIds(Set<Long> users) {
        return users != null && !users.isEmpty() && users.iterator().next() != 0 ? QEvent.event.initiator.id.in(users) : null;
    }

    private BooleanExpression byDates(LocalDateTime start, LocalDateTime end) {
        return start != null && end != null ? QEvent.event.eventDate.after(start).and(QEvent.event.eventDate.before(end)) : null;
    }

    private BooleanExpression byDatesWithDefaults(LocalDateTime start, LocalDateTime end) {
        return start != null && end != null ? QEvent.event.eventDate.after(start).and(QEvent.event.eventDate.before(end)) : QEvent.event.eventDate.after(LocalDateTime.now());
    }

    private BooleanExpression byText(String text) {
        return text != null && !text.equals("0") ? QEvent.event.annotation.containsIgnoreCase(text) : null;
    }

    private BooleanExpression byPaid(Boolean paid) {
        return paid != null ? QEvent.event.paid.eq(paid) : null;
    }

    private BooleanExpression byPublishedEvents() {
        return QEvent.event.state.eq(EventState.PUBLISHED);
    }

    private BooleanExpression byOnlyAvailable(Boolean onlyAvailable) {
        return onlyAvailable != null && onlyAvailable ? QEvent.event.confirmedRequests.lt(QEvent.event.participantLimit) : null;
    }

}
