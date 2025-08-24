package ru.yandex.practicum.service;

import ru.yandex.practicum.CreateHitDto;
import ru.yandex.practicum.ResponseHitDto;
import ru.yandex.practicum.ResponseStatsDto;

import java.util.List;

public interface StatisticService {
    ResponseHitDto create(CreateHitDto createHitDto);

    List<ResponseStatsDto> get(String start, String end, List<String> uris, Boolean unique);
}