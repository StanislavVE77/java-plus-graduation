package ru.yandex.practicum.ewm.event;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewm.event.dto.EventFullDto;
import ru.yandex.practicum.ewm.event.service.EventService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class EventInternalController {
    private final EventService eventService;

    @PutMapping("/{id}")
    public ResponseEntity<EventFullDto> updateInternal(@RequestBody @Valid EventFullDto eventUpdateDto,
                                               @PathVariable("id") Long eventId) {
        log.info("--> PATCH запрос /events/{} с телом {}", eventId, eventUpdateDto);
        EventFullDto event = eventService.updateInternal(eventId, eventUpdateDto);
        log.info("<-- PATCH запрос /events/{} вернул ответ: {}", eventId, event);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(event);
    }

    @GetMapping("/{id}/internal")
    public ResponseEntity<EventFullDto> getByIdInternal(@PathVariable("id") Long eventId) {
        log.info("--> GET запрос /events/{}/internal", eventId);
        EventFullDto event = eventService.getByIdInternal(eventId);
        log.info("<-- GET запрос /events/{}/internal вернул ответ: {}", eventId, event);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(event);
    }

}
