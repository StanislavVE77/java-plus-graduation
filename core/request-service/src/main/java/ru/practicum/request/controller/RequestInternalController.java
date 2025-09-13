package ru.practicum.request.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.model.Request;
import ru.practicum.request.service.RequestService;

import java.util.List;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class RequestInternalController {
    private final RequestService requestService;

    @GetMapping("/events/{eventId}")
    public ResponseEntity<List<Request>> getByEventId(@PathVariable Long eventId) {
        List<Request> requests = requestService.getByEventId(eventId);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(requests);
    }

    @GetMapping("/events/{eventId}/set")
    public ResponseEntity<List<Request>> getByEventIdAndIds(@PathVariable Long eventId, @RequestParam Set<Long> requestIds) {
        List<Request> requests = requestService.getByEventIdAndIds(eventId, requestIds);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(requests);
    }

    @PutMapping
    public ResponseEntity<Request> updateInternal(@RequestBody Request request) {
        Request updRequest = requestService.updateInternal(request);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updRequest);

    }
}
