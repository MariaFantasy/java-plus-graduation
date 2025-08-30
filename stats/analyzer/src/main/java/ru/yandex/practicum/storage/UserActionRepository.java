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
                SELECT MAX(innerUa.actionWeight)
                FROM UserAction AS innerUa
                WHERE innerUa.eventId = ua.eventId
                    AND innerUa.userId = ua.userId
            )
    """)
    List<UserAction> getMaxWeightedForEvents(List<Long> eventId);

    List<UserAction> findAllByUserId(Long userId);

}
