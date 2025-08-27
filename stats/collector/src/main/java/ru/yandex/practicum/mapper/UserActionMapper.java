package ru.yandex.practicum.mapper;

import com.google.protobuf.Timestamp;
import org.mapstruct.Mapper;
import ru.yandex.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.yandex.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.grpc.stats.action.ActionTypeProto;
import ru.yandex.practicum.grpc.stats.action.UserActionProto;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface UserActionMapper {
    UserActionAvro mapToAvro(UserActionProto action);

    default ActionTypeAvro mapActionType(ActionTypeProto actionType) {
        final ActionTypeAvro mappedActionType = switch (actionType) {
            case ACTION_VIEW -> ActionTypeAvro.VIEW;
            case ACTION_REGISTER -> ActionTypeAvro.REGISTER;
            case ACTION_LIKE -> ActionTypeAvro.LIKE;
            default -> throw new IllegalArgumentException("Unknown User Action Type: " + actionType);
        };
        return mappedActionType;
    }

    default Instant mapTimestamp(Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
}
