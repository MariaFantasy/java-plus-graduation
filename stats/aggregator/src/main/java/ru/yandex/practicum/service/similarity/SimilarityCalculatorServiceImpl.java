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
            log.info("Записываем вес события {} для пользователя {}: значение={}", eventId, userId, weight);
            eventVector.put(userId, 0.);
            log.info("Weights:{}", weights.toString());
            log.info("Event Dot Product:{}", eventDotProduct.toString());
            final List<EventSimilarityAvro> updatedSimilarity = recalculate(eventId, userId, weight);
            eventVector.put(userId, weight);
            log.info("Weights:{}", weights.toString());
            log.info("Event Dot Product:{}", eventDotProduct.toString());
            log.info("Записанное значение:{}\nЗаписанные похожие события:{}", eventVector.get(userId), updatedSimilarity);
            return updatedSimilarity;
        } else if (eventVector.get(userId) < weight) {
            log.info("Обновляем вес события {} для пользователя {}: старое значение={}, новое значение={}", eventId, userId, eventVector.get(userId), weight);
            log.info("Weights:{}", weights.toString());
            log.info("Event Dot Product:{}", eventDotProduct.toString());
            final List<EventSimilarityAvro> updatedSimilarity = recalculate(eventId, userId, weight);
            eventVector.put(userId, weight);
            log.info("Weights:{}", weights.toString());
            log.info("Event Dot Product:{}", eventDotProduct.toString());
            log.info("Записанное значение:{}\nЗаписанные похожие события:{}", eventVector.get(userId), updatedSimilarity);
            return updatedSimilarity;
        }
        log.info("Weights:{}", weights.toString());
        log.info("Event Dot Product:{}", eventDotProduct.toString());
        log.info("Вес события {} для пользователя {} не обновляется: старое значение={}, новое значение={}", eventId, userId, eventVector.get(userId), weight);
        return List.of();
    }

    private List<EventSimilarityAvro> recalculate(Long eventId, Long userId, Double weight) {
        // Обновляем диагональ (скалярное произведение события на себя же)
        if (!eventDotProduct.containsKey(eventId)) {
            eventDotProduct.put(eventId, new HashMap<>());
        }
        if (!eventDotProduct.get(eventId).containsKey(eventId)) {
            eventDotProduct.get(eventId).put(eventId, weight);
        } else if (eventDotProduct.get(eventId).get(eventId) < weight) {
            eventDotProduct.get(eventId).put(eventId, weight);
        }

        // Обновляем остальные элементы матрицы, с колонкой или строчкой, равной eventId
        List<EventSimilarityAvro> updatedSimilarity = new ArrayList<>();
        long eventA;
        long eventB;
        boolean isLess;
        for (Long otherEventId: weights.keySet()) {
            if (otherEventId.equals(eventId)) {
                continue;
            }
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
            log.info("A={}, B={}, userId={}", eventA, eventB, userId);
            log.info("weightA={}, weightB={}", weightA, weightB);
            double oldValue = Math.min(weightA, weightB);
            double newValue = Math.min(weight, isLess ? weightB : weightA);
            log.info("oldValue={}, newValue={}", oldValue, newValue);
            if (Math.abs(oldValue - newValue) >= 0.01) {
                log.info("Old similarity coefficient={}", eventAVector.get(eventB));
                eventAVector.put(eventB, eventAVector.get(eventB) + newValue - oldValue);
                log.info("New similarity coefficient={}", eventAVector.get(eventB));
                double eventADenominator = Math.sqrt(eventDotProduct.get(eventA).get(eventA));
                double eventBDenominator = Math.sqrt(eventDotProduct.get(eventB).get(eventB));
                log.info("Event A denominator={}, event B denominator={}", eventADenominator, eventBDenominator);
                updatedSimilarity.add(new EventSimilarityAvro(eventA, eventB, eventAVector.get(eventB) / eventADenominator / eventBDenominator, Instant.now()));
            }
        }
        return updatedSimilarity;
    }
}
