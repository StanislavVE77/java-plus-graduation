package ru.practicum.user.service;

import ru.practicum.user.dto.UserCreateDto;
import ru.practicum.user.dto.UserRequestDto;

import java.util.List;

public interface UserService {
    UserRequestDto create(UserCreateDto userCreateDto);

    List<UserRequestDto> get(List<Long> ids, int from, int size);

    void delete(Long userId);
}
