package ru.yandex.practicum.service.user_action;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.mapper.model.UserActionMapper;
import ru.yandex.practicum.model.UserAction;
import ru.yandex.practicum.storage.UserActionRepository;

import java.util.List;

@Service("userActionServiceImpl")
@RequiredArgsConstructor
public class UserActionServiceImpl implements UserActionService {
    private final UserActionRepository userActionRepository;
    private final UserActionMapper userActionMapper;

    @Override
    public void saveUserAction(UserActionAvro userActionAvro) {
        final UserAction userAction = userActionMapper.mapFromAvro(userActionAvro);
        userActionRepository.save(userAction);
    }

    @Override
    public List<UserAction> getMaxWeightedForEvents(List<Long> eventIds) {
        return userActionRepository.getMaxWeightedForEvents(eventIds);
    }

    @Override
    public List<UserAction> getByUser(Long userId) {
        return userActionRepository.findAllByUserId(userId);
    }
}
