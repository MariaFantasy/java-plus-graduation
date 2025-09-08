package ru.yandex.practicum.service;

import ru.yandex.practicum.grpc.stats.action.UserActionProto;

public interface CollectorService {
    void loadUserAction(UserActionProto userAction);
}
