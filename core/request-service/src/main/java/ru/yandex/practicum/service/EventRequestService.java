package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.dto.EventRequestStatusUpdateResult;
import ru.yandex.practicum.dto.ParticipationRequestDto;

import java.util.Collection;

public interface EventRequestService {

    ParticipationRequestDto create(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest requestsToUpdate);

    ParticipationRequestDto cancel(Long userId, Long requestId);

    Collection<ParticipationRequestDto> getByRequesterId(Long requesterId);

    Collection<ParticipationRequestDto> getByEventId(Long eventInitiatorId, Long eventId);
}
