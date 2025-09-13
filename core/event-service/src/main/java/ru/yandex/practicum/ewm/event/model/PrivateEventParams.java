package ru.yandex.practicum.ewm.event.model;

import lombok.Data;

@Data
public class PrivateEventParams {
    private Long userId;
    private Integer from;
    private Integer size;

}
