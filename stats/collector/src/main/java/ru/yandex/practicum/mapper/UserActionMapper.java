package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.grpc.stats.action.UserActionProto;

@Mapper(componentModel = "spring")
public interface UserActionMapper {
    UserActionAvro mapToAvro(UserActionProto action);
}
