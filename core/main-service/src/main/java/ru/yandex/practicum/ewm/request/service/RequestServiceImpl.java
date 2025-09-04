package ru.yandex.practicum.ewm.request.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.event.model.Event;
import ru.yandex.practicum.ewm.event.model.EventState;
import ru.yandex.practicum.ewm.event.storage.EventRepository;
import ru.yandex.practicum.ewm.exception.CompilationNotFoundException;
import ru.yandex.practicum.ewm.exception.ConflictException;
import ru.yandex.practicum.ewm.exception.EventNotFoundException;
import ru.yandex.practicum.ewm.request.dto.RequestDto;
import ru.yandex.practicum.ewm.request.mapper.RequestMapper;
import ru.yandex.practicum.ewm.request.model.Request;
import ru.yandex.practicum.ewm.request.model.RequestStatus;
import ru.yandex.practicum.ewm.request.storage.RequestRepository;
import ru.yandex.practicum.ewm.user.model.User;
import ru.yandex.practicum.ewm.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public RequestDto create(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CompilationNotFoundException(userId));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

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
        request.setRequester(user);
        request.setEvent(event);
        request.setCreated(LocalDateTime.now());

        if (!event.getRequestModeration()) {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);

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
        userRepository.findById(userId)
                .orElseThrow(() -> new CompilationNotFoundException(userId));

        List<Request> requests = requestRepository.findAllByRequesterId(userId);

        return requests.stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public RequestDto update(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new CompilationNotFoundException(userId));

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new CompilationNotFoundException(requestId));

        request.setStatus(RequestStatus.CANCELED);

        return RequestMapper.toDto(
                requestRepository.save(request)
        );
    }
}
