package ru.practicum.stats.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventSimilarityService {
    private final Map<Long, Map<Long, Double>> minWeightsSums = new HashMap<>();
    private final Map<Long, Map<Long, Double>> weightMatrix = new HashMap<>();
    private final Map<Long, Double> weightSumForEvent = new HashMap<>();



    public List<EventSimilarityAvro> calculate(UserActionAvro userAction) {
        List<EventSimilarityAvro> result = new ArrayList<>();

        log.info("Состояние матрицы весов начальное: {}", weightMatrix);
        Long eventId = userAction.getEventId();
        Long userId = userAction.getUserId();
        String userActionType = userAction.getActionType().toString();
        Double rating = userActionType.equals("ACTION_LIKE") ? 1.0 : (
                userActionType.equals("ACTION_REGISTER") ? 0.8 : (
                        userActionType.equals("ACTION_VIEW") ? 0.4 : 0.0
                )
        );
        log.info("Получен вес \"{}\" из нового сообщения {}", rating, userAction);
        Map<Long, Double> userWeightInMatrix = weightMatrix.get(eventId);
        if (userWeightInMatrix == null) {
            log.info("Добавляем новую запись в матрицу весов. Новый пользователь у события {}", eventId);
            Map<Long, Double> userWeightCurrent = new HashMap<>();
            userWeightCurrent.put(userId, rating);
            weightMatrix.put(eventId, userWeightCurrent);
            weightSumForEvent.put(eventId, rating);
            result = calculateSimilarity(eventId, userId);

        } else {
            Double weightInMatrix = weightMatrix.get(eventId).get(userId);
            if (weightInMatrix == null || rating > weightInMatrix) {
                log.info("Добавляем новую запись в матрицу весов. Новый вес {} вместо {} для пользователя {} у события {}", rating, weightInMatrix, userId, eventId);
                userWeightInMatrix.put(userId, rating);
                        weightMatrix.put(eventId, userWeightInMatrix);
                weightSumForEvent.put(eventId, calculateWeightSumForEvent(userWeightInMatrix));
                result = calculateSimilarity(eventId, userId);


            } else {
                log.info("Ничего не делаем");
            }
        }
        log.info("Состояние матрицы весов по завершению: {}", weightMatrix);
        log.info("Cумма весов каждого мероприятия: {}", weightSumForEvent);
        log.info("--------------------------------------------------------------------------------------------------------------------------");
        return result;
    }


    private Double calculateWeightSumForEvent (Map<Long, Double> userWeightInMatrix) {
        Double sum = 0.0;
        for (Double weight : userWeightInMatrix.values()) {
            sum = sum + weight;
        }
        return Math.sqrt(sum);
    }

    private List<EventSimilarityAvro> calculateSimilarity (Long eventId, Long baseUserId) {
        List<EventSimilarityAvro> result = new ArrayList<>();
        Double similarity = 0.0;
        Double weightSum1 = calculateWeightSumForEvent(weightMatrix.get(eventId));
        Set<Long> userIds = weightMatrix.get(eventId).keySet();
        log.info("userIds={}", userIds);
        for (Long curEventId : weightMatrix.keySet()) {
            if (curEventId != eventId) {
                Double weightSum2 = calculateWeightSumForEvent(weightMatrix.get(curEventId));
                Set<Long> curUserIds = weightMatrix.get(curEventId).keySet();
                log.info("curUserIds={}", curUserIds);
                if (curUserIds.contains(baseUserId)) {
                    Double sum = 0.0;
                    for (Long userId : userIds) {
                        for (Long curUserId : curUserIds) {
                            if (userId == curUserId) {

                                sum += Math.min(weightMatrix.get(eventId).get(userId), weightMatrix.get(curEventId).get(curUserId));
                                log.info("*** sum={}, прибавляем min({},{})", sum, weightMatrix.get(eventId).get(userId), weightMatrix.get(curEventId).get(curUserId));
                            }
                        }
                    }

                    if (sum > 0) {
                        similarity = sum / (weightSum1 * weightSum2);
                        log.info("** Рассчет схожести для мероприятий {} и {} : c данными: sum={}, weightSum1={}, weightSum2={}", eventId, curEventId, sum, weightSum1, weightSum2);
                        put(eventId, curEventId, similarity);
                        BigDecimal similaritybd = new BigDecimal(similarity);
                        similaritybd = similaritybd.setScale(2, RoundingMode.HALF_UP);
                        EventSimilarityAvro msg = EventSimilarityAvro.newBuilder()
                                .setEventA(Long.valueOf(Math.min(eventId, curEventId)).intValue())
                                .setEventB(Long.valueOf(Math.max(eventId, curEventId)).intValue())
                                .setScore(similaritybd.doubleValue())
                                .setTimestamp(Instant.now())
                                .build();
                        log.info("** формирование сообщения EventSimilarityAvro, msg={}", msg);
                        result.add(msg);
                        log.info("** добавление сообщения EventSimilarityAvro в result");

                    }
                } else {
                    log.info("Пользователь {} не взаимодействовал с мероприятием {}", baseUserId, curEventId);
                }
            }
        }
        log.info("* Матрица схожести: {}", minWeightsSums);
        log.info("* Результат: {}", result);
        return result;
    }

    public void put(long eventA, long eventB, double sum) {
        long first  = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);

        minWeightsSums
                .computeIfAbsent(first, e -> new HashMap<>())
                .put(second, sum);
    }

    public double get(long eventA, long eventB) {
        long first  = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);

        return minWeightsSums
                .computeIfAbsent(first, e -> new HashMap<>())
                .getOrDefault(second, 0.0);
    }
}
