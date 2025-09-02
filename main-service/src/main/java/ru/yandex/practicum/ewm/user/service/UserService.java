package ru.yandex.practicum.ewm.user.service;

import ru.yandex.practicum.ewm.user.dto.UserCreateDto;
import ru.yandex.practicum.ewm.user.dto.UserRequestDto;

import java.util.List;

public interface UserService {
    UserRequestDto create(UserCreateDto userCreateDto);

    List<UserRequestDto> get(List<Integer> ids, int from, int size);

    void delete(Long userId);
}
