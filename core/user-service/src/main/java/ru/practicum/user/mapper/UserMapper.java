package ru.practicum.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.user.dto.UserCreateDto;
import ru.practicum.user.dto.UserRequestDto;
import ru.practicum.user.model.User;

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
