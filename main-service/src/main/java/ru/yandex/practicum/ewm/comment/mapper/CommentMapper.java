package ru.yandex.practicum.ewm.comment.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.ewm.comment.dto.CommentDtoRequest;
import ru.yandex.practicum.ewm.comment.dto.CommentDtoResponse;
import ru.yandex.practicum.ewm.comment.model.Comment;
import ru.yandex.practicum.ewm.event.dto.EventShortDto;
import ru.yandex.practicum.ewm.event.model.Event;
import ru.yandex.practicum.ewm.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import ru.yandex.practicum.ewm.user.dto.UserShortDto;

@Component
public class CommentMapper {

    public static CommentDtoResponse toDto(Comment comment) {
        return new CommentDtoResponse(
                comment.getId(),
                comment.getCreated(),
                toEventShortDto(comment.getEvent()),
                toUserShortDto(comment.getUser()),
                comment.getText()
        );
    }

    public static List<CommentDtoResponse> toDto(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }

    public static Comment toEntity(CommentDtoRequest dto, Event eventEntity, User userEntity, LocalDateTime createdEntity) {
        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setEvent(eventEntity);
        comment.setUser(userEntity);
        comment.setCreated(createdEntity);
        return comment;
    }

    private static EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .build();
    }

    private static UserShortDto toUserShortDto(User user) {
        return new UserShortDto(user.getId(), user.getName());
    }
}
