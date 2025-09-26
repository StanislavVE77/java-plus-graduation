package ru.yandex.practicum.ewm.event;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.grpc.stats.recommendations.RecommendedEventProto;
import ru.yandex.practicum.ewm.event.dto.EventFullDto;
import ru.yandex.practicum.ewm.event.service.EventService;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class EventInternalController {
    private final EventService eventService;

    @PutMapping("/{id}")
    public ResponseEntity<EventFullDto> updateInternal(@RequestBody @Valid EventFullDto eventUpdateDto,
                                               @PathVariable("id") Long eventId) {
        log.info("--> PUT запрос /events/{} с телом {}", eventId, eventUpdateDto);
        EventFullDto event = eventService.updateInternal(eventId, eventUpdateDto);
        log.info("<-- PUT запрос /events/{} вернул ответ: {}", eventId, event);
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

    @PutMapping("/{id}/like")
    public ResponseEntity<EventFullDto> setLike(@RequestHeader("X-EWM-USER-ID") long userId,
                                                       @PathVariable("id") Long eventId) {
        log.info("--> PUT запрос /events/{}/like для пользователя {}", userId);
        EventFullDto event = eventService.setLike(eventId, userId);
        log.info("<-- PUT запрос /events/{}/like вернул ответ: {}", eventId, event);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(event);
    }

    @GetMapping("/{id}/similar")
    public ResponseEntity<List<EventFullDto>> getSimilarEvents(@RequestHeader("X-EWM-USER-ID") long userId,
                                                               @PathVariable("id") Long eventId,
                                                               @RequestParam(defaultValue = "10") int maxResults) {
        log.info("--> GET запрос /events/{}/similar для пользователя {}", userId);
        List<EventFullDto> events = eventService.getSimilarEvents(eventId, userId, maxResults);
        log.info("<-- GET запрос /events/{}/similar вернул ответ: {}", eventId, events);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(events);
    }

    @GetMapping("/recommendations")
    public  ResponseEntity<List<EventFullDto>> getRecommendations(@RequestHeader("X-EWM-USER-ID") long userId,
                                        @RequestParam(defaultValue = "10") int maxResults) {
        log.info("--> GET запрос /events/recommendations для пользователя {}", userId);
        List<EventFullDto> events = eventService.getRecommendations(userId, maxResults);
        log.info("<-- GET запрос /events/recommendations вернул ответ: {}", events);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(events);

    }

    @GetMapping("/interactions")
    public  ResponseEntity<List<Double>> getInteractions(@RequestParam(required = false) Set<Long> eventsIds) {
        log.info("--> GET запрос /events/interactions?eventsIds={}", eventsIds);
        List<RecommendedEventProto> results = eventService.getInteractions(eventsIds);
        log.info("<-- GET запрос /events/interactions?eventsIds={} вернул ответ: {}", eventsIds, results);
        List<Double> scores = results.stream()
                .map(RecommendedEventProto::getScore)
                .toList();
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(scores);

    }
}
