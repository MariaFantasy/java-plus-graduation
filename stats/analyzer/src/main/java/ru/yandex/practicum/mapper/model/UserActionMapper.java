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
    @Mapping(target = "actionType", source = "actionType")
    @Mapping(target = "actionWeight", expression = "java(mapWeight(userActionAvro.getActionType()))")
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

    default double mapWeight(ActionTypeAvro actionType) {
        return switch (actionType) {
            case VIEW -> 0.4;
            case REGISTER -> 0.8;
            case LIKE -> 1.0;
            default -> throw new IllegalArgumentException("Unknown User Action Type: " + actionType);
        };
    }
}
