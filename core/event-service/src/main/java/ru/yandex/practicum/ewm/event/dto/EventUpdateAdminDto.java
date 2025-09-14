package ru.yandex.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.ewm.event.model.EventStateAction;
import ru.yandex.practicum.ewm.event.model.Location;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class EventUpdateAdminDto {

    @Size(max = 2000, min = 20, message = "Annotation length must be between 20 and 2000 characters")
    private String annotation;
    private Long category;
    @Size(max = 7000, min = 20, message = "Description length must be between 20 and 7000 characters")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;

    @PositiveOrZero(message = "Number must be zero or positive")
    private Integer participantLimit;
    private Boolean requestModeration;
    private EventStateAction stateAction;
    @Size(max = 120, min = 3, message = "Title length must be between 3 and 120 characters")
    private String title;
}
