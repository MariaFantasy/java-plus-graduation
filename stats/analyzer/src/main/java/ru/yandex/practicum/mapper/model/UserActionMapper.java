package ru.yandex.practicum.mapper.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.yandex.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.model.ActionType;
import ru.yandex.practicum.model.UserAction;

@Mapper(componentModel = "spring")
public interface UserActionMapper {
    @Mapping(target = "id", ignore = true)
    UserAction mapFromAvro(UserActionAvro userActionAvro);

    default ActionType mapActionType(ActionTypeAvro actionType) {
        final ActionType mappedActionType = switch (actionType) {
            case VIEW -> ActionType.VIEW;
            case REGISTER -> ActionType.REGISTER;
            case LIKE -> ActionType.LIKE;
            default -> throw new IllegalArgumentException("Unknown User Action Type: " + actionType);
        };
        return mappedActionType;
    }
}
