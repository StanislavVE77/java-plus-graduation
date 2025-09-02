package ru.yandex.practicum.ewm.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewm.event.dto.EventFullDto;
import ru.yandex.practicum.ewm.event.dto.EventUpdateAdminDto;
import ru.yandex.practicum.ewm.event.model.AdminEventParams;
import ru.yandex.practicum.ewm.event.model.EventState;
import ru.yandex.practicum.ewm.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class EventAdminController {
    private final EventService eventService;

    @PatchMapping("/{id}")
    public ResponseEntity<EventFullDto> update(@RequestBody @Valid EventUpdateAdminDto eventUpdateDto,
                                          @PathVariable("id") Long eventId) {
        log.info("--> PATCH запрос /admin/events/{} с телом {}", eventId, eventUpdateDto);
        EventFullDto event = eventService.update(eventId, eventUpdateDto);
        log.info("<-- PATCH запрос /admin/events/{} вернул ответ: {}", eventId, event);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(event);
    }


    @GetMapping
    public ResponseEntity<List<EventFullDto>> get(
            @RequestParam(required = false) Set<Long> users,
            @RequestParam(required = false) Set<EventState> states,
            @RequestParam(required = false) Set<Long> categories,
            @RequestParam(required = false)  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false)  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        AdminEventParams adminEventParams = new AdminEventParams();
        log.info("- new AdminEventParams -");
        adminEventParams.setUsers(users);
        adminEventParams.setStates(states);
        adminEventParams.setCategories(categories);
        adminEventParams.setRangeStart(rangeStart);
        adminEventParams.setRangeEnd(rangeEnd);
        adminEventParams.setFrom(from);
        adminEventParams.setSize(size);
        log.info("--> GET запрос /admin/events с параметрами {}", adminEventParams);
        List<EventFullDto> events = eventService.getAdmin(adminEventParams);
        log.info("<-- GET запрос /admin/events вернул ответ: {}", events);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(events);
    }

}
