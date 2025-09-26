package ru.practicum.stats.analyzer.dal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.stats.analyzer.dal.model.UserAction;
import ru.practicum.stats.analyzer.dal.repository.InteractionsRepository;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActionService {
    private final InteractionsRepository repository;

    public void handleRecord(UserActionAvro record) throws InterruptedException {
        log.info("Обработка записи: {}", record);

        Long userId = (long) record.getUserId();
        Long eventId = (long) record.getEventId();
        String userActionType = record.getActionType().toString();
        Double rating = userActionType.equals("ACTION_LIKE") ? 1.0 : (
                userActionType.equals("ACTION_REGISTER") ? 0.8 : (
                        userActionType.equals("ACTION_VIEW") ? 0.4 : 0.0
                )
        );
        Instant ts = record.getTimestamp();
        log.info("Поиск по userId={} и eventId={} записи в таблице interactions.", userId, eventId);
        UserAction userAction = repository.findByUserIdAndEventId(userId, eventId);
        if (userAction == null) {
            saveUserAction(null, userId, eventId, rating, ts);
            log.info("Добавление новой записи Record=\"{}\" в таблицу interactions.", record);
        } else {
            if (userAction.getRating() < rating) {
                saveUserAction(userAction.getId(), userAction.getUserId(), userAction.getEventId(), rating, ts);
                log.info("Изменение записи c id={} в таблице interactions. Record: {}", userAction.getId(), record);
            }
        }
    }

    private void saveUserAction(Long id, Long userId, Long eventId, Double rating, Instant ts) {
        UserAction userAction = new UserAction();
        if (id != null) {
            userAction.setId(id);
        }
        userAction.setUserId(userId);
        userAction.setEventId(eventId);
        userAction.setRating(rating);
        userAction.setTs(ts);
        repository.save(userAction);
    }
}
