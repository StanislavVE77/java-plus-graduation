package ru.yandex.practicum.ewm.comment.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.comment.dto.CommentDtoRequest;
import ru.yandex.practicum.ewm.comment.dto.CommentDtoResponse;
import ru.yandex.practicum.ewm.comment.mapper.CommentMapper;
import ru.yandex.practicum.ewm.comment.model.Comment;
import ru.yandex.practicum.ewm.comment.service.specification.DbCommentSpecification;
import ru.yandex.practicum.ewm.comment.storage.CommentRepository;
import ru.yandex.practicum.ewm.event.model.Event;
import ru.yandex.practicum.ewm.event.model.EventState;
import ru.yandex.practicum.ewm.event.storage.EventRepository;
import ru.yandex.practicum.ewm.exception.*;
import ru.yandex.practicum.ewm.user.model.User;
import ru.yandex.practicum.ewm.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentServiceImpl implements CommentService {

    final CommentRepository commentRepository;
    final EventRepository eventRepository;
    final UserRepository userRepository;
    final CommentMapper commentMapper;

    @Override
    public CommentDtoResponse create(Long userId, Long eventId, CommentDtoRequest dto) {
        Event event = getPublishedEvent(eventId);
        User user = getUser(userId);
        Comment comment = commentRepository.save(commentMapper.toEntity(dto, event, user, LocalDateTime.now()));
        return commentMapper.toDto(comment);
    }

    @Override
    public CommentDtoResponse update(Long userId, Long eventId, Long commId, CommentDtoRequest dto) {
        Comment comment = getValidComment(userId, eventId, commId);
        comment.setText(dto.getText());
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public void delete(Long userId, Long eventId, Long commId) {
        getValidComment(userId, eventId, commId);
        commentRepository.deleteById(commId);
    }

    @Override
    public CommentDtoResponse findCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
        return commentMapper.toDto(comment);
    }

    @Override
    public List<CommentDtoResponse> findCommentsByEventId(Long eventId) {
        validateEventExists(eventId);
        return commentMapper.toDto(commentRepository.findAllByEventId(eventId));
    }

    @Override
    public List<CommentDtoResponse> findCommentsByUserIdAndEventId(Long userId, Long eventId) {
        validateEventExists(eventId);
        validateUserExists(userId);
        return commentMapper.toDto(commentRepository.findAllByUserIdAndEventId(userId, eventId));
    }

    @Override
    public List<CommentDtoResponse> findCommentsAdmin(List<Integer> users, List<Integer> events,
                                                      LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                      Integer from, Integer size) {
        Specification<Comment> spec = DbCommentSpecification.getSpecificationAdmin(users, events, rangeStart, rangeEnd);
        Pageable pageable = PageRequest.of(from / size, size);
        return commentMapper.toDto(commentRepository.findAll(spec, pageable).getContent());
    }

    @Override
    public void deleteCommentAdmin(Long commId) {
        if (!commentRepository.existsById(commId)) {
            throw new CommentNotFoundException(commId);
        }
        commentRepository.deleteById(commId);
    }

    private Event getPublishedEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        if (event.getState() != EventState.PUBLISHED) {
            throw new EventDateException("Event with id=" + eventId + " is not published");
        }

        return event;
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private void validateEventExists(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException(eventId);
        }
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
    }

    private Comment getValidComment(Long userId, Long eventId, Long commId) {
        validateEventExists(eventId);
        validateUserExists(userId);

        Comment comment = commentRepository.findById(commId)
                .orElseThrow(() -> new CommentNotFoundException(commId));

        if (!Objects.equals(comment.getUser().getId(), userId)) {
            throw new ConflictException("User with id=" + userId + " is not the author of the comment");
        }

        return comment;
    }
}
