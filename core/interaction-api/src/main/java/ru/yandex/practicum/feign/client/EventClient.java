package ru.yandex.practicum.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.EventFullDto;

@FeignClient(name="event-service", path="/admin/events")
public interface EventClient {

    @GetMapping("/{eventId}")
    EventFullDto getById(@PathVariable Long eventId);

    @PostMapping("/confirmed")
    void updateEventConfirmedRequests(@RequestParam Long eventId, @RequestParam Long confirmedRequests);

}
