package ru.practicum.request.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.EventFullDto;

@FeignClient(name = "event-service")
public interface EventClient {

    @GetMapping("/events/{id}")
    EventFullDto getById(@RequestHeader("X-EWM-USER-ID") long userId, @PathVariable("id") Long eventId);

    @PutMapping("/events/{id}")
    EventFullDto updateInternal(@RequestBody EventFullDto eventUpdateDto, @PathVariable("id") Long eventId);
}
