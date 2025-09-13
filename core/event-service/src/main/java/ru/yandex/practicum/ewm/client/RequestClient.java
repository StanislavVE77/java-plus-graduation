package ru.yandex.practicum.ewm.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewm.request.model.Request;

import java.util.List;
import java.util.Set;

@FeignClient(name = "request-service", path = "/requests")
public interface RequestClient {

    @GetMapping("/events/{eventId}")
    List<Request> getByEventId(@PathVariable Long eventId);

    @GetMapping("/events/{eventId}/set")
    List<Request> getByEventIdAndIds(@PathVariable Long eventId, @RequestParam Set<Long> requestIds);

    @PutMapping
    Request updateInternal(@RequestBody Request request);

}
