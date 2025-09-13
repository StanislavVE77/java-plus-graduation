package ru.yandex.practicum.ewm.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewm.event.dto.*;
import ru.yandex.practicum.ewm.event.model.PrivateEventParams;
import ru.yandex.practicum.ewm.event.service.EventService;
import ru.yandex.practicum.ewm.request.dto.RequestEventDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class EventPrivateController {
    private final EventService eventService;

    @GetMapping("/{userId}/events")
    public ResponseEntity<List<EventShortDto>> get(
            @PathVariable("userId") Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        PrivateEventParams privateEventParams = new PrivateEventParams();
        privateEventParams.setUserId(userId);
        privateEventParams.setFrom(from);
        privateEventParams.setSize(size);
        log.info("--> GET запрос /users/{}/events с параметрами {}", userId, privateEventParams);
        List<EventShortDto> events = eventService.getPrivate(privateEventParams);
        log.info("<-- GET запрос /users/{}/events вернул ответ: {}", userId, events);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(events);
    }

    @PostMapping("/{userId}/events")
    public ResponseEntity<EventFullDto> create(@PathVariable("userId") Long userId,
                                            @RequestBody @Valid EventCreateDto eventDto) {
        log.info("--> POST запрос /users/{}/events с телом {}", userId, eventDto);
        EventFullDto event = eventService.create(userId, eventDto);
        log.info("<-- POST запрос /users/{}/events вернул ответ: {}", userId, event);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(event);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> getById(@PathVariable("userId") Long userId,
                                                @PathVariable("eventId") Long eventId) {
        log.info("--> GET запрос /users/{}/events/{}", userId, eventId);
        EventFullDto event = eventService.getByIdPrivate(userId, eventId);
        log.info("<-- GET запрос /users/{}/events/{} вернул ответ: {}", userId, eventId, event);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(event);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> update(@RequestBody @Valid EventUpdateUserDto eventUpdateDto,
                                               @PathVariable("userId") Long userId,
                                               @PathVariable("eventId") Long eventId) {
        log.info("--> PATCH запрос /users/{}/events/{} с телом {}", userId, eventId, eventUpdateDto);
        EventFullDto event = eventService.updatePrivate(userId, eventId, eventUpdateDto);
        log.info("<-- PATCH запрос /users/{}/events/{} вернул ответ: {}", userId, eventId, event);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(event);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<List<RequestEventDto>> getAllRequests(@PathVariable("userId") Long userId,
                                                                @PathVariable("eventId") Long eventId) {
        log.info("--> GET запрос /users/{}/events/{}/requests", userId, eventId);
        List<RequestEventDto> requests = eventService.getRequestsByIdPrivate(userId, eventId);
        log.info("<-- GET запрос /users/{}/events/{}/requests вернул ответ: {}", userId, eventId, requests);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(requests);
    }


    @PatchMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<EventResultRequestStatusDto> update(@RequestBody @Valid EventUpdateRequestStatusDto updateDto,
                                               @PathVariable("userId") Long userId,
                                               @PathVariable("eventId") Long eventId) {
        log.info("--> PATCH запрос /user/{}/events/{}/requests с телом {}", userId, eventId, updateDto);
        EventResultRequestStatusDto result = eventService.updateRequestStatusPrivate(userId, eventId, updateDto);
        log.info("<-- PATCH запрос /user/{}/events/{}/requests вернул ответ: {}", userId, eventId, result);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }

}
