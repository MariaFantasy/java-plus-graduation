package ru.yandex.practicum.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.stats.event_recommendations.InteractionsCountRequestProto;
import ru.yandex.practicum.grpc.stats.event_recommendations.RecommendedEventProto;
import ru.yandex.practicum.grpc.stats.event_recommendations.SimilarEventsRequestProto;
import ru.yandex.practicum.grpc.stats.event_recommendations.UserPredictionsRequestProto;
import ru.yandex.practicum.model.EventSimilarity;
import ru.yandex.practicum.model.UserAction;
import ru.yandex.practicum.service.event_similarity.EventSimilarityService;
import ru.yandex.practicum.service.user_action.UserActionService;

import java.util.*;
import java.util.stream.Collectors;

@Service("analyzerServiceImpl")
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {
    private static final int K_NEAREST_NEIGHBOURS = 10;

    private final UserActionService userActionService;
    private final EventSimilarityService eventSimilarityService;

    public List<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto request) {
//        final List<UserAction> userActions = userActionService.getByUser(request.getUserId());
//        // Оставляем действие с событием с максимальным весом.
//        final Map<Long, Double> seenEvents = userActions.stream()
//                .collect(Collectors.groupingBy(
//                        UserAction::getEventId,
//                        Collectors.collectingAndThen(
//                            Collectors.mapping(UserAction::getActionWeight, Collectors.maxBy(Double::compare)),
//                            optional -> optional.orElse(0.0)
//                        )
//                ));
//
//        // Находим похожие события к уже просмотренным пользователем.
//        final List<EventSimilarity> eventSimilarities = eventSimilarityService.getSimilarToEvents(seenEvents.keySet().stream().toList());
//        final Map<Long, Double> similarEvents = eventSimilarities.stream()
//                .collect(Collectors.toMap(
//                        EventSimilarity::getEventBId,
//                        EventSimilarity::getScore
//                ));
//
//        // Убираем уже просмотренные пользователем события.
//        // Сортируем по убыванию похожести (score) и оставляем только первые maxResults.
//        final List<Long> filteredEvents = similarEvents.keySet().stream()
//                .filter(e -> !seenEvents.containsKey(e))
//                .sorted(Comparator.comparing(similarEvents::get).reversed())
//                .limit(request.getMaxResults())
//                .collect(Collectors.toCollection(ArrayList::new));
//
//        // К каждому событию из похожих находим их похожие записи.
//        final List<EventSimilarity> similarEventsForFiltered = eventSimilarityService.getSimilarToEvents(filteredEvents);
//
//        // Убираем из списка события, которые клиент не видел.
//        final Map<Long, Map<Long, Double>> eventXNeighbourEvent = similarEventsForFiltered.stream()
//            .filter(e -> seenEvents.containsKey(e.getEventBId()))
//            .collect(Collectors.groupingBy(
//                    EventSimilarity::getEventAId,
//                    Collectors.toMap(
//                            EventSimilarity::getEventBId,
//                            EventSimilarity::getScore
//                    )
//            ));
//
//        // Сортируем по score по убыванию похожести и оставляем только K из них.
//        // Рассчитываем для оставшихся взвешенную оценку и суммируем.
//        final Map<Long, Double> weightedEvents = filteredEvents.stream()
//                .map(e -> {
//                    final Map<Long, Double> events = eventXNeighbourEvent.get(e);
//                    if (events.size() == 0) {
//                        return Map.entry(e, 0.0);
//                    }
//                    double weightedCoefficients = events.entrySet().stream()
//                            .sorted(Comparator.comparing(events::get).reversed())
//                            .limit(K_NEAREST_NEIGHBOURS)
//                            .map(event -> events.get(event) * seenEvents.get(event))
//                            .mapToDouble(Double::doubleValue)
//                            .sum();
//                    double coefficients = events.entrySet().stream()
//                            .sorted(Comparator.comparing(events::get).reversed())
//                            .limit(K_NEAREST_NEIGHBOURS)
//                            .map(events::get)
//                            .mapToDouble(Double::doubleValue)
//                            .sum();
//                    return Map.entry(e, weightedCoefficients / coefficients);
//                })
//                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//
//        return weightedEvents.keySet().stream()
//            .map(e -> RecommendedEventProto.newBuilder()
//                .setEventId(e)
//                .setScore(weightedEvents.get(e))
//                .build()
//        )
//                .collect(Collectors.toCollection(ArrayList::new));
        throw new RuntimeException("Start getRecommendationsForUser");
    }

    public List<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request) {
//        final List<UserAction> userActions = userActionService.getByUser(request.getUserId());
//        final Set<Long> seenEvents = userActions.stream()
//                .map(UserAction::getEventId)
//                .collect(Collectors.toSet());
//
//        final List<EventSimilarity> eventSimilarities = eventSimilarityService.getSimilarToEvent(request.getEventId());
//        final Map<Long, Double> similarEvents = eventSimilarities.stream()
//                        .collect(Collectors.toMap(
//                                EventSimilarity::getEventBId,
//                                EventSimilarity::getScore
//                        ));
//
//        return similarEvents.keySet().stream()
//            .filter(e -> !seenEvents.contains(e))
//            .sorted(Comparator.comparing(similarEvents::get).reversed())
//            .limit(request.getMaxResults())
//            .map(e -> RecommendedEventProto.newBuilder()
//                    .setEventId(e)
//                    .setScore(similarEvents.get(e))
//                    .build()
//            )
//            .collect(Collectors.toCollection(ArrayList::new));
        throw new RuntimeException("Start getSimilarEvents");
    }

    public List<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request) {
        final List<Long> eventIds = request.getEventIdList();
        final List<UserAction> userActions = userActionService.getMaxWeightedForEvents(eventIds);
        final Map<Long, Double> eventsWithSum = userActions.stream()
                .collect(Collectors.groupingBy(
                        UserAction::getEventId,
                        Collectors.summingDouble(UserAction::getActionWeight)
                ));
        return eventsWithSum.keySet().stream()
                .map(e -> RecommendedEventProto.newBuilder()
                        .setEventId(e)
                        .setScore(eventsWithSum.get(e))
                        .build()
                )
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
