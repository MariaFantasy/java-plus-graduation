package ru.yandex.practicum.service.event_similarity;

import ru.yandex.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.yandex.practicum.model.EventSimilarity;

import java.util.List;

public interface EventSimilarityService {

    void saveEventSimilarity(EventSimilarityAvro eventSimilarityAvro);

    List<EventSimilarity> getSimilarToEvent(Long eventId);

    List<EventSimilarity> getSimilarToEvents(List<Long> eventIds);

}
