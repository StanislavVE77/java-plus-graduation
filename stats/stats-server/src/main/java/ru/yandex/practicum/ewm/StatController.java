package ru.yandex.practicum.ewm;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.yandex.practicum.ewm.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping
public class StatController {

    private final StatsService statsService;

    @PostMapping("/hit")
    public ResponseEntity<String> saveHit(@RequestBody @Valid HitDto hitDto) {
        statsService.save(hitDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Информация сохранена");
    }

    @GetMapping("/stats")
    public ResponseEntity<List<StatsDto>> getStats(
            @RequestParam  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")  LocalDateTime start,
            @RequestParam  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")  LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") boolean unique
    ) {
        return statsService.getStats(start, end, uris, unique);
    }
}
