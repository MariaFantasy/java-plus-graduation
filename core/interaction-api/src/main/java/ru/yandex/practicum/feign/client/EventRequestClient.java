package ru.yandex.practicum.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="request-service", path="/users/{userId}")
public interface EventRequestClient {
    @GetMapping("/events/{eventId}/isregister")
    Boolean isUserRegisterOnEvent(@RequestParam Long userId, @RequestParam Long eventId);
}
