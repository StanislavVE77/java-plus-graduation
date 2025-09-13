package ru.practicum.request.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.request.dto.EventFullDto;

@FeignClient(name = "event-service")
public interface EventClient {

    @GetMapping("/events/{id}")
    EventFullDto getById(@PathVariable("id") Long eventId);

    @PutMapping("/events/{id}")
    EventFullDto updateInternal(@RequestBody EventFullDto eventUpdateDto, @PathVariable("id") Long eventId);
}
