package ru.yandex.practicum.ewm.request;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewm.request.dto.RequestDto;
import ru.yandex.practicum.ewm.request.service.RequestService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/users/{userId}/requests")
public class RequestPrivateController {

    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<RequestDto> create(@PathVariable Long userId,
                                             @RequestParam Long eventId) {
        RequestDto requestDto = requestService.create(userId, eventId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestDto);
    }

    @GetMapping
    public ResponseEntity<List<RequestDto>> get(@PathVariable Long userId) {
        List<RequestDto> requests = requestService.get(userId);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(requests);
    }

    @PatchMapping("{requestId}/cancel")
    public ResponseEntity<RequestDto> update(@PathVariable Long userId,
                                             @PathVariable Long requestId) {
        RequestDto requestDto = requestService.update(userId, requestId);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestDto);
    }
}
