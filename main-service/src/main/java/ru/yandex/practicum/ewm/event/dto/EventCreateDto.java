package ru.yandex.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.ewm.event.model.Location;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventCreateDto {
    @NotBlank(message = "Annotation cannot be blank.")
    @Size(max = 2000, min = 20, message = "Annotation length must be between 20 and 2000 characters")
    private String annotation;

    private Long category;

    @NotBlank(message = "Description cannot be blank.")
    @Size(max = 7000, min = 20, message = "Description length must be between 20 and 7000 characters")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created = LocalDateTime.now();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @FutureOrPresent()
    private LocalDateTime eventDate;

    private Location location;

    private Boolean paid;

    @PositiveOrZero(message = "Number must be zero or positive")
    private Integer participantLimit;

    private Boolean requestModeration;

    @NotBlank(message = "Title cannot be blank.")
    @Size(max = 120, min = 3, message = "Title length must be between 3 and 120 characters")
    private String title;

    private Integer views = 0;
}
