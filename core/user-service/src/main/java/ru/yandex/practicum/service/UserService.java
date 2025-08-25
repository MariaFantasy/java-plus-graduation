package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.NewUserRequest;
import ru.yandex.practicum.dto.UserDto;
import ru.yandex.practicum.dto.UserShortDto;

import java.util.Collection;
import java.util.List;

public interface UserService {

    UserDto create(NewUserRequest newUserRequest);

    Collection<UserDto> findAll(List<Long> ids, Integer from, Integer size);

    Collection<UserShortDto> findAllInShort(List<Long> ids, Integer from, Integer size);

    UserShortDto findById(Long userId);

    void delete(Long userId);
}
