package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.ParticipationRequestDto;
import ru.yandex.practicum.service.EventRequestService;

@Slf4j
@RestController
@RequestMapping("/users/{userId}")
@RequiredArgsConstructor
public class EventRequestController {
    private final EventRequestService eventRequestService;

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto create(@PathVariable Long userId, @RequestParam Long eventId) {
        log.info("Пришел POST запрос /users/{}/requests?eventId={}", userId, eventId);
        final ParticipationRequestDto request = eventRequestService.create(userId, eventId);
        log.info("Отправлен ответ POST /users/{}/requests?eventId={} с телом: {}", userId, eventId, request);
        return request;
    }
}
