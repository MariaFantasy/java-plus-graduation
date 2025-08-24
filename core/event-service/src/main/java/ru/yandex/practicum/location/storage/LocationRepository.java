package ru.yandex.practicum.location.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.location.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
