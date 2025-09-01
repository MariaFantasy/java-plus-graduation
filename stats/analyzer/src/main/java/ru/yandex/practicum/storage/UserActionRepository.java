package ru.yandex.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.model.UserAction;

import java.util.List;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {

    @Query("""
        SELECT
            ua.eventId,
            ua.userId,
            MAX(ua.actionWeight) as actionWeight,
            MAX(ua.timestamp) as timestamp
        FROM UserAction AS ua
        WHERE ua.eventId IN ?1
        GROUP BY ua.eventId, ua.userId
    """)
    List<UserAction> getMaxWeightedForEvents(List<Long> eventId);

    List<UserAction> findAllByUserId(Long userId);

}
