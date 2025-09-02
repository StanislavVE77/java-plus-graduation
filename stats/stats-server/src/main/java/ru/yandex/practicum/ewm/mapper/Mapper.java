package ru.yandex.practicum.ewm.mapper;

import ru.practicum.dto.HitDto;
import ru.yandex.practicum.ewm.model.Hit;

public class Mapper {

    public static Hit toEntityHit(HitDto hitDto) {
        Hit hit = new Hit();
        hit.setApp(hitDto.getApp());
        hit.setUri(hitDto.getUri());
        hit.setIp(hitDto.getIp());
        hit.setTimestamp(hitDto.getTimestamp());

        return hit;
    }
}
