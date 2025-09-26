package ru.practicum.stats.analyzer.dal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.stats.analyzer.dal.model.EventSimilarity;
import ru.practicum.stats.analyzer.dal.repository.SimilaritiesRepository;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventSimilarityService {
    private final SimilaritiesRepository repository;

    public void handleRecord(EventSimilarityAvro record) throws InterruptedException {
        log.info("Обработка записи: {}", record);

        Long eventA = (long) record.getEventA();
        Long eventB = (long) record.getEventB();
        Double score = record.getScore();
        Instant ts = record.getTimestamp();
        EventSimilarity dbRecord;
        if (eventA != eventB) {
            if (eventA > eventB) {
                log.info("Поиск по event1={} и event2={} записи в таблице similarities.", eventB, eventA);
                dbRecord = repository.findByEventAAndEventB(eventB, eventA);
            } else {
                log.info("Поиск по event1={} и event2={} записи в таблице similarities.", eventA, eventB);
                dbRecord = repository.findByEventAAndEventB(eventA, eventB);
            }

            EventSimilarity result = new EventSimilarity();
            if (dbRecord == null) {
                result.setEventA(Math.min(eventA, eventB));
                result.setEventB(Math.max(eventA, eventB));
                result.setSimilarity(record.getScore());
                result.setTs(record.getTimestamp());
                repository.save(result);
                log.info("Добавление новой записи Record=\"{}\" в таблицу similarities.", result);
            } else {
                result.setId(dbRecord.getId());
                result.setEventA(dbRecord.getEventA());
                result.setEventB(dbRecord.getEventB());
                result.setSimilarity(record.getScore());
                result.setTs(record.getTimestamp());
                repository.save(result);
                log.info("Изменение записи c id={} в таблице similarities. Record: {}", dbRecord.getId(), result);
            }
        } else {
            log.info("Идентификаторы event1={} и event2={} одинаковы.", eventA, eventB);
        }
    }


}
