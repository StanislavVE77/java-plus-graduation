package ru.yandex.practicum.ewm.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRequestDto {
    private Long id;
    private String name;
    private String email;
}
