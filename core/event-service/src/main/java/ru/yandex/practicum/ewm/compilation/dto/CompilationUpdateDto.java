package ru.yandex.practicum.ewm.compilation.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CompilationUpdateDto {
    @Size(min = 3, max = 50, message = "Name length must be between 3 and 51 characters")
    private String title;

    private Boolean pinned;

    private List<Long> events;

}
