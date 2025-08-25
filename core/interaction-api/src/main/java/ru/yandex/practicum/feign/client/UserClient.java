package ru.yandex.practicum.feign.client;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.UserShortDto;

import java.util.Collection;
import java.util.List;

@FeignClient(name="user-service", path="/admin/users")
public interface UserClient {
    @GetMapping("/short")
    public Collection<UserShortDto> getShort(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = "10") @Positive Integer size
    );

    @GetMapping("/{userId}")
    public UserShortDto getById(@PathVariable Long userId);
}
