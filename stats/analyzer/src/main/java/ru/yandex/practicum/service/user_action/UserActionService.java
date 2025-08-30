package ru.yandex.practicum.service.user_action;

import ru.yandex.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.model.UserAction;

import java.util.List;

public interface UserActionService {
    void saveUserAction(UserActionAvro userActionAvro);

    List<UserAction> getMaxWeightedForEvents(List<Long> eventIds);

    List<UserAction> getByUser(Long userId);
}
