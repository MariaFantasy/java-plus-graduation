package ru.yandex.practicum.service.event_similarity;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.yandex.practicum.mapper.model.EventSimilarityMapper;
import ru.yandex.practicum.model.EventSimilarity;
import ru.yandex.practicum.storage.EventSimilarityRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service("eventSimilarityServiceImpl")
@RequiredArgsConstructor
public class EventSimilarityServiceImpl implements EventSimilarityService {
    private final EventSimilarityRepository eventSimilarityRepository;
    private final EventSimilarityMapper eventSimilarityMapper;

    @Override
    public void saveEventSimilarity(EventSimilarityAvro eventSimilarityAvro) {
        final EventSimilarity eventSimilarity = eventSimilarityMapper.mapFromAvro(eventSimilarityAvro);
        eventSimilarityRepository.save(eventSimilarity);
    }

    @Override
    public List<EventSimilarity> getSimilarToEvent(Long eventId) {
        final List<EventSimilarity> similaritiesByA = eventSimilarityRepository.findByEventAId(eventId);
        final List<EventSimilarity> similaritiesByB = eventSimilarityRepository.findByEventBId(eventId);
        final List<EventSimilarity> similaritiesByBSwapped = similaritiesByB.stream()
                .map(s -> new EventSimilarity(s.getId(), s.getEventBId(), s.getEventAId(), s.getScore(), s.getTimestamp()))
                .collect(Collectors.toCollection(ArrayList::new));
        similaritiesByA.addAll(similaritiesByBSwapped);
        return similaritiesByA;
    }

    @Override
    public List<EventSimilarity> getSimilarToEvents(List<Long> eventIds) {
        final List<EventSimilarity> similaritiesByA = eventSimilarityRepository.findByEventAId(eventIds);
        final List<EventSimilarity> similaritiesByB = eventSimilarityRepository.findByEventBId(eventIds);
        final List<EventSimilarity> similaritiesByBSwapped = similaritiesByB.stream()
                .map(s -> new EventSimilarity(s.getId(), s.getEventBId(), s.getEventAId(), s.getScore(), s.getTimestamp()))
                .collect(Collectors.toCollection(ArrayList::new));
        similaritiesByA.addAll(similaritiesByBSwapped);
        return similaritiesByA;
    }
}
