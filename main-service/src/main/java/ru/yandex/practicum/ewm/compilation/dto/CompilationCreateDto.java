package ru.yandex.practicum.ewm.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CompilationCreateDto {
    @NotBlank(message = "Title can not be blank")
    @Size(min = 3, max = 50, message = "Name length must be between 3 and 50 characters")
    private String title;

    private Boolean pinned;

    private List<Long> events;
}
