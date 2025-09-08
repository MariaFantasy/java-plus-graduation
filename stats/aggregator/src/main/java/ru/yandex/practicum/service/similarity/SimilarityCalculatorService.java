package ru.yandex.practicum.service.similarity;

import ru.yandex.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.yandex.practicum.ewm.stats.avro.UserActionAvro;

import java.util.List;

public interface SimilarityCalculatorService {
    List<EventSimilarityAvro> getSimilarityEvent(UserActionAvro avro);
}
