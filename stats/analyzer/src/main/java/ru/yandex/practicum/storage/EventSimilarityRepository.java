package ru.yandex.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.model.EventSimilarity;

import java.util.List;

public interface EventSimilarityRepository extends JpaRepository<EventSimilarity, Long> {

    List<EventSimilarity> findByEventAId(Long eventId);

    @Query("""
        SELECT es
        FROM EventSimilarity AS es
        WHERE eventAId IN ?1
    """)
    List<EventSimilarity> findByEventAId(List<Long> eventId);

    List<EventSimilarity> findByEventBId(Long eventId);

    @Query("""
        SELECT es
        FROM EventSimilarity AS es
        WHERE eventBId IN ?1
    """)
    List<EventSimilarity> findByEventBId(List<Long> eventId);

}
