package ru.practicum.stats.analyzer.service.handler;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.grpc.stats.recommendations.RecommendedEventProto;
import ru.practicum.stats.analyzer.dal.model.EventSimilarity;
import ru.practicum.stats.analyzer.dal.model.UserAction;
import ru.practicum.stats.analyzer.dal.repository.InteractionsRepository;
import ru.practicum.stats.analyzer.dal.repository.SimilaritiesRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class EventSimilarityHandler {
    private static final int K = 5;

    private final InteractionsRepository interactionsRepository;
    private final SimilaritiesRepository similaritiesRepository;

    public List<RecommendedEventProto> getRecommendationsForUser(Long userId, long maxResults) {
        List<UserAction> userEventList = interactionsRepository.findByUserId(userId);

        Set<Long> userEventsFull = userEventList.stream()
                .map(UserAction::getEventId)
                .collect(Collectors.toSet());
        Set<Long> userEvents = userEventList.stream()
                .sorted(Comparator.comparing(UserAction::getTs).reversed())
                .map(UserAction::getEventId)
                .limit(maxResults)
                .collect(Collectors.toSet());


        if (userEvents.isEmpty()) {
            return List.of();
        }
        List<UserAction> notUserEventList = interactionsRepository.findNotByUserId(userId);
        Set<Long> notUserEvents = notUserEventList.stream()
                .map(UserAction::getEventId)
                .collect(Collectors.toSet());

        List<EventSimilarity> eventSimilarityList = new ArrayList<>();
        List<EventSimilarity> allEventSimilarityList = similaritiesRepository.findAll();
        for (EventSimilarity eventSimilarity : allEventSimilarityList) {
            if ((userEvents.contains(eventSimilarity.getEventA()) && notUserEvents.contains(eventSimilarity.getEventB())) ||
                    (userEvents.contains(eventSimilarity.getEventB()) && notUserEvents.contains(eventSimilarity.getEventA()))) {
                eventSimilarityList.add(eventSimilarity);
            }
        }
        List<EventSimilarity> sortedSimilarityList = eventSimilarityList.stream()
                .sorted(Comparator.comparing(EventSimilarity::getSimilarity).reversed())
                .limit(maxResults)
                .toList();

        Map<Long, Double> sortedEvents = new HashMap<>();
        for (EventSimilarity eventSimilarity : sortedSimilarityList) {
            if (notUserEvents.contains(eventSimilarity.getEventA())) {
                if (!sortedEvents.keySet().contains(eventSimilarity.getEventA())) {
                    sortedEvents.put(eventSimilarity.getEventA(), eventSimilarity.getSimilarity());
                }
            } else {
                if (!sortedEvents.keySet().contains(eventSimilarity.getEventB())) {
                    sortedEvents.put(eventSimilarity.getEventB(), eventSimilarity.getSimilarity());
                }
            }
            if (sortedEvents.size() == maxResults) {
                break;
            }
        }

        List<RecommendedEventProto> results = new ArrayList<>();
        for (Long eventId : sortedEvents.keySet()) {

            List<EventSimilarity> simRecList = allEventSimilarityList.stream()
                    .filter(u -> ((u.getEventA() == eventId) && (userEventsFull.contains(u.getEventB()))) || (u.getEventB() == eventId && (userEventsFull.contains(u.getEventA()))))
                    .sorted(Comparator.comparing(EventSimilarity::getSimilarity).reversed())
                    .limit(K)
                    .toList();

            Set<Long> eventsFromSimRecList = simRecList.stream()
                    .map(u -> userEventsFull.contains(u.getEventA()) ? u.getEventA() : u.getEventB())
                    .collect(Collectors.toSet());

            List<UserAction> scoreRecList = interactionsRepository.findByEventIdIn(eventsFromSimRecList).stream()
                    .filter(u -> u.getUserId() == userId)
                    .toList();

            double sumWeightedEstimates = simRecList.stream()
                    .flatMap(eventSim -> scoreRecList.stream()
                            .filter(userAction -> userAction.getEventId() == eventSim.getEventA() ||
                                    userAction.getEventId() == eventSim.getEventB())
                            .map(userAction -> userAction.getRating() * eventSim.getSimilarity())
                    )
                    .mapToDouble(Double::doubleValue)
                    .sum();
            double coefSum = simRecList.stream()
                    .map(EventSimilarity::getSimilarity)
                    .mapToDouble(Double::doubleValue)
                    .sum();

            double score = sumWeightedEstimates / coefSum;

            BigDecimal bd = new BigDecimal(score);
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            double scoreFormat = bd.doubleValue();

            results.add(RecommendedEventProto.newBuilder()
                    .setEventId(eventId)
                    .setScore(scoreFormat)
                    .build()
            );
        }

        return results;
    }

    public List<RecommendedEventProto> getSimilarEvents(Long userId, Long eventId, long maxResults) {
        List<EventSimilarity> eventSimilarityList = similaritiesRepository.findByEventAOrEventB(eventId);
        List<UserAction> userEventList = interactionsRepository.findByUserId(userId);
        Set<Long> userItemIds = userEventList.stream()
                .map(UserAction::getEventId)
                .collect(Collectors.toSet());
        List<EventSimilarity> resultEventSimilarityList = eventSimilarityList.stream()
                .filter(eventSimilarity -> !userItemIds.contains(eventSimilarity.getEventA()) ||
                        !userItemIds.contains(eventSimilarity.getEventB()))
                .toList();

        return resultEventSimilarityList.stream()
                .map(c -> mapperToProto(c.getEventA() == eventId ? c.getEventB() : c.getEventA(), c.getSimilarity()))
                .sorted(Comparator.comparingDouble(RecommendedEventProto::getScore).reversed())
                .limit(maxResults)
                .collect(Collectors.toList());
    }

    public List<RecommendedEventProto> getInteractionsCount(Set<Long> eventsIds) {
        List<UserAction> userActionList = interactionsRepository.findByEventIdIn(eventsIds);
        List<RecommendedEventProto> recEventProtoList = new ArrayList<>();
        for (Long curEventId : eventsIds) {
            List<UserAction> curUserActionList = userActionList.stream()
                    .filter(ua -> ua.getEventId() == curEventId)
                    .toList();
            Double sum = sumScoresByEvent(curUserActionList);
            recEventProtoList.add(mapperToProto(curEventId, sum));

        }
        return recEventProtoList.stream()
                .sorted(Comparator.comparingDouble(RecommendedEventProto::getScore).reversed())
                .collect(Collectors.toList());
    }

    private Double sumScoresByEvent(List<UserAction> userActionList) {
        return userActionList.stream()
                .mapToDouble(UserAction::getRating)
                .sum();
    }

    private RecommendedEventProto mapperToProto(Long eventId, Double sum) {
        return RecommendedEventProto.newBuilder()
                .setEventId(eventId)
                .setScore(sum)
                .build();
    }

}
