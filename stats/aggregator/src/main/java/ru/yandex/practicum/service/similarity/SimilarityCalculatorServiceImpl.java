package ru.yandex.practicum.service.similarity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.yandex.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.yandex.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SimilarityCalculatorServiceImpl implements SimilarityCalculatorService {
    // Матрица весов. Событие: Клиент: Вес
    private final Map<Long, Map<Long, Double>> weights = new HashMap<>();
    // Событие A: Событие B: скалярное произведение векторов (основано на минимальном весе)
    // По сути является диагональной матрицей. Для экономии памяти и упрощения расчетов хранит только значения элементов
    // на диагонали и над ней.
    // В случае, когда событие A совпадает с событием B, можно не считать отдельно сумму знаменателя - это значение
    // уже есть в этой матрице на диагонали.
    private final Map<Long, Map<Long, Double>> eventDotProduct = new HashMap<>();

    public List<EventSimilarityAvro> getSimilarityEvent(UserActionAvro avro) {
        final Double weight = switch (avro.getActionType()) {
            case ActionTypeAvro.VIEW -> 0.4;
            case ActionTypeAvro.REGISTER -> 0.8;
            case ActionTypeAvro.LIKE -> 1.0;
            default -> 0.0;
        };
        return updateWeight(avro.getEventId(), avro.getUserId(), weight);
    }

    private List<EventSimilarityAvro> updateWeight(Long eventId, Long userId, Double weight) {
        if (!weights.containsKey(eventId)) {
            weights.put(eventId, new HashMap<>());
        }
        final Map<Long, Double> eventVector = weights.get(eventId);
        if (!eventVector.containsKey(userId)) {
            eventVector.put(userId, 0.);
            final List<EventSimilarityAvro> updatedSimilarity = recalculate(eventId, userId, weight);
            eventVector.put(userId, weight);
            return updatedSimilarity;
        } else if (eventVector.get(userId) < weight) {
            final List<EventSimilarityAvro> updatedSimilarity = recalculate(eventId, userId, weight);
            eventVector.put(userId, weight);
            return updatedSimilarity;
        }
        return List.of();
    }

    private List<EventSimilarityAvro> recalculate(Long eventId, Long userId, Double weight) {
        List<EventSimilarityAvro> updatedSimilarity = new ArrayList<>();
        long eventA;
        long eventB;
        boolean isLess;
        for (Long otherEventId: weights.keySet()) {
            isLess = eventId < otherEventId;
            if (isLess) {
                eventA = eventId;
                eventB = otherEventId;
            } else {
                eventA = otherEventId;
                eventB = eventId;
            }
            if (!eventDotProduct.containsKey(eventA)) {
                eventDotProduct.put(eventA, new HashMap<>());
            }
            final Map<Long, Double> eventAVector = eventDotProduct.get(eventA);
            if (!eventAVector.containsKey(eventB)) {
                eventAVector.put(eventB, 0.);
            }
            double weightA = weights.get(eventA).getOrDefault(userId, 0.0);
            double weightB = weights.get(eventB).getOrDefault(userId, 0.0);
            double oldValue = Math.min(weightA, weightB);
            double newValue = Math.min(weight, isLess ? weightA : (eventA == eventB) ? weight : weightB);
            if (oldValue != newValue) {
                eventAVector.put(eventB, eventAVector.get(eventB) + newValue - oldValue);
                if (eventA != eventB) {
                    updatedSimilarity.add(new EventSimilarityAvro(eventA, eventB, eventAVector.get(eventB), Instant.now()));
                }
            }
        }
        return updatedSimilarity;
    }
}
