package ru.yandex.practicum.ewm.event.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class AdminEventParams {
    private Set<Long> users;
    private Set<EventState> states;
    private Set<Long> categories;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Integer from;
    private Integer size;

}
