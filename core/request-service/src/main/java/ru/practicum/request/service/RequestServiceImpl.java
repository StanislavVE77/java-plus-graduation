package ru.practicum.request.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.request.client.UserClient;
import ru.practicum.request.client.EventClient;
import ru.practicum.request.dto.EventFullDto;
import ru.practicum.request.model.EventState;
import ru.practicum.request.exception.CompilationNotFoundException;
import ru.practicum.request.exception.ConflictException;
import ru.practicum.request.exception.EventNotFoundException;
import ru.practicum.request.exception.UserNotFoundException;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.storage.RequestRepository;
import ru.practicum.request.dto.UserRequestDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserClient userClient;
    private final EventClient eventClient;

    @Override
    public RequestDto create(Long userId, Long eventId) {
        UserRequestDto user = userClient.getUsersById(List.of(userId)).getFirst();
        if (user == null) {
            throw new UserNotFoundException(userId);
        }
        EventFullDto event;
        try {
            event = eventClient.getById(eventId);
        } catch (Exception e) {
            throw new ConflictException("You cannot register in an unpublished event.");
        }

        if (event == null) {
            throw new EventNotFoundException(eventId);
        }

        if (user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException("You cannot register for your own event.");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("You cannot register in an unpublished event.");
        }

        if (event.getConfirmedRequests().equals(event.getParticipantLimit()) && event.getParticipantLimit() != 0) {
            throw new ConflictException("All spots are taken, registration is not possible.");
        }

        Request request = new Request();
        request.setRequesterId(userId);
        request.setEventId(eventId);
        request.setCreated(LocalDateTime.now());

        if (!event.getRequestModeration()) {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            EventFullDto updEvent = eventClient.updateInternal(event, eventId);

        } else {
            request.setStatus(RequestStatus.PENDING);
        }

        if (event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        }


        return RequestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public List<RequestDto> get(Long userId) {
        UserRequestDto user = userClient.getUsersById(List.of(userId)).getFirst();
        if (user == null) {
            throw new UserNotFoundException(userId);
        }

        List<Request> requests = requestRepository.findAllByRequesterId(userId);

        return requests.stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public RequestDto update(Long userId, Long requestId) {
        UserRequestDto user = userClient.getUsersById(List.of(userId)).getFirst();
        if (user == null) {
            throw new UserNotFoundException(userId);
        }

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new CompilationNotFoundException(requestId));

        request.setStatus(RequestStatus.CANCELED);

        return RequestMapper.toDto(
                requestRepository.save(request)
        );
    }

    @Override
    public Request updateInternal(Request request) {
        return requestRepository.save(request);
    }

    @Override
    public List<Request> getByEventId(Long eventId) {
        return requestRepository.findAllByEventId(eventId);
    }

    @Override
    public List<Request> getByEventIdAndIds(Long eventId, Set<Long> requestsIds) {
        return requestRepository.findAllByEventIdAndIdIn(eventId, requestsIds);

    }
}
