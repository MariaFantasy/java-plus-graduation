package ru.yandex.practicum.location.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.LocationDto;
import ru.yandex.practicum.location.mapper.LocationDtoMapper;
import ru.yandex.practicum.location.model.Location;
import ru.yandex.practicum.location.storage.LocationRepository;

@Service("locationServiceImpl")
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;
    private final LocationDtoMapper locationDtoMapper;

    @Override
    public LocationDto create(LocationDto locationDto) {
        final Location location = locationDtoMapper.mapFromDto(locationDto);
        final Location createdLocation = locationRepository.save(location);
        return locationDtoMapper.mapToDto(createdLocation);
    }
}
