package ru.yandex.practicum.ewm.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserCreateDto {
    @NotBlank(message = "Name can not be blank")
    @Size(min = 2, max = 250, message = "Name length must be between 2 and 250 characters")
    private String name;

    @Email(message = "Invalid email")
    @NotBlank(message = "Email can not be blank")
    @Size(min = 6, max = 254, message = "Name length must be between 6 and 254 characters")
    private String email;
}
