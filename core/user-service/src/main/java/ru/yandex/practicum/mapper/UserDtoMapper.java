package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.dto.NewUserRequest;
import ru.yandex.practicum.dto.UserDto;
import ru.yandex.practicum.dto.UserShortDto;
import ru.yandex.practicum.model.User;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {

    UserDto mapToDto(User user);

    UserShortDto mapToShortDto(User user);

    @Mapping(target = "id", ignore = true)
    User mapFromDto(NewUserRequest newUserRequest);

    User mapFromDto(UserDto userDto);

    @Mapping(target = "email", ignore = true)
    User mapFromDto(UserShortDto userDto);
}
