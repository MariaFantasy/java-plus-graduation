package ru.yandex.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.model.UserAction;

import java.util.List;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {

    @Query("""
        SELECT ua
        FROM UserAction AS ua
        WHERE ua.eventId IN ?1
          AND ua.actionWeight = (
              SELECT MAX(ua2.actionWeight)
              FROM UserAction AS ua2
              WHERE ua2.eventId = ua.eventId
                AND ua2.userId = ua.userId
          )
          AND ua.timestamp = (
              SELECT MAX(ua3.timestamp)
              FROM UserAction AS ua3
              WHERE ua3.eventId = ua.eventId
                AND ua3.userId = ua.userId
                AND ua3.actionWeight = ua.actionWeight
          )
    """)
    List<UserAction> getMaxWeightedForEvents(List<Long> eventId);

    List<UserAction> findAllByUserId(Long userId);

}
