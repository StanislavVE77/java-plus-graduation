package ru.yandex.practicum.ewm.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.practicum.ewm.event.dto.EventShortDto;
import ru.yandex.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class CommentDtoResponse {
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    private EventShortDto event;

    private UserShortDto user;

    private String text;
}
