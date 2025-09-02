package ru.yandex.practicum.ewm.user.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.ewm.user.dto.UserCreateDto;
import ru.yandex.practicum.ewm.user.dto.UserRequestDto;
import ru.yandex.practicum.ewm.user.model.User;

@Component
public class UserMapper {
    public static User toEntity(UserCreateDto userCreateDto) {
        User user = new User();
        user.setEmail(userCreateDto.getEmail());
        user.setName(userCreateDto.getName());

        return user;
    }

    public static UserRequestDto toRequestDto(User user) {
        return new UserRequestDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
