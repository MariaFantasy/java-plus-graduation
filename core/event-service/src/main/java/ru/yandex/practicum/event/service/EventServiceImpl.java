package ru.yandex.practicum.event.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.yandex.practicum.category.mapper.CategoryDtoMapper;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.category.service.CategoryService;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.event.mapper.EventDtoMapper;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.storage.EventRepository;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.ForbiddenException;
import ru.yandex.practicum.exception.IncorrectRequestException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.feign.client.UserClient;
import ru.yandex.practicum.location.mapper.LocationDtoMapper;
import ru.yandex.practicum.location.model.Location;
import ru.yandex.practicum.location.service.LocationService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service("eventServiceImpl")
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private static final String APP_NAME = "main_svc";
    private static final String STATS_SERVICE_SCHEME = "http";
    private static final String STATS_SERVICE_NAME = "STATS-SERVER";
    private final UserClient userClient;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final EventRepository eventRepository;
    private final EventDtoMapper eventDtoMapper;
    private final CategoryDtoMapper categoryDtoMapper;
    private final LocationDtoMapper locationDtoMapper;
    private final RestTemplate restTemplate;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public EventFullDto create(Long userId, NewEventDto eventDto) {
        final UserShortDto user = userClient.getById(userId);

        validateEventDate(eventDto.getEventDate());
        final Event event = eventDtoMapper.mapFromDto(eventDto);
        event.setInitiatorId(user.getId());
        final Event createdEvent = eventRepository.save(event);

        return eventDtoMapper.mapToFullDto(createdEvent, user);
    }

    @Override
    public Collection<EventShortDto> findAllByPublic(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size, HttpServletRequest request) {
        if (rangeStart != null && rangeEnd != null && LocalDateTime.parse(rangeStart, formatter).isAfter(LocalDateTime.parse(rangeEnd, formatter))) {
            throw new IncorrectRequestException("RangeStart is after Range End");
        }
        if (sort != null && !sort.equals("EVENT_DATE") && !sort.equals("VIEWS")) {
            throw new IncorrectRequestException("Unknown sort type");
        }
        saveView(request);
        log.info("Saved view");
        final Collection<Event> events = eventRepository.findAllByPublic(text, categories, paid, rangeStart == null ? null : LocalDateTime.parse(rangeStart, formatter), rangeEnd == null ? null : LocalDateTime.parse(rangeEnd, formatter), onlyAvailable, (Pageable) PageRequest.of(from, size));
        final Collection<UserShortDto> usersDto = userClient.getShort(events.stream().map(Event::getInitiatorId).toList(), 0, 10);
        final Map<Long, UserShortDto> usersInfo = usersDto.stream().collect(Collectors.toMap(UserShortDto::getId, u -> u));
        return events.stream()
                .map(event -> {
                    final EventShortDto eventDto = eventDtoMapper.mapToShortDto(event, usersInfo.get(event.getInitiatorId()));
                    eventDto.setViews(countViews(event.getId(), event.getCreatedOn(), LocalDateTime.now()));
                    return eventDto;
                })
                .sorted((e1, e2) -> sort == null || sort.equals("EVENT_DATE") ? e1.getEventDate().compareTo(e2.getEventDate()) : e1.getViews().compareTo(e2.getViews()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Collection<EventShortDto> findAllByPrivate(Long userId, Integer from, Integer size) {
        final UserShortDto user = userClient.getById(userId);
        final Collection<Event> events = eventRepository.findAllByInitiatorId(user.getId(), PageRequest.of(from, size));
        return events.stream()
                .map(event -> {
                    final EventShortDto eventDto = eventDtoMapper.mapToShortDto(event, user);
                    eventDto.setViews(countViews(event.getId(), event.getCreatedOn(), LocalDateTime.now()));
                    return eventDto;
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Collection<EventFullDto> findAllByAdmin(List<Long> users, List<String> states, List<Long> categories, String rangeStart, String rangeEnd, Integer from, Integer size) {
        final Collection<Event> events = eventRepository.findAllByAdmin(users, states, categories, rangeStart == null ? null : LocalDateTime.parse(rangeStart, formatter), rangeEnd == null ? null : LocalDateTime.parse(rangeEnd, formatter), (Pageable) PageRequest.of(from, size));
        final Collection<UserShortDto> usersDto = userClient.getShort(users, 0, 10);
        final Map<Long, UserShortDto> usersInfo = usersDto.stream().collect(Collectors.toMap(UserShortDto::getId, u -> u));
        return events.stream()
                .map(event -> {
                    final EventFullDto eventDto = eventDtoMapper.mapToFullDto(event, usersInfo.get(event.getInitiatorId()));
                    eventDto.setViews(countViews(event.getId(), event.getCreatedOn(), LocalDateTime.now()));
                    return eventDto;
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<EventShortDto> findAllById(List<Long> eventIds) {
        final Collection<Event> events = eventRepository.findAllById(eventIds);
        final Collection<UserShortDto> usersDto = userClient.getShort(events.stream().map(Event::getInitiatorId).toList(), 0, 10);
        final Map<Long, UserShortDto> usersInfo = usersDto.stream().collect(Collectors.toMap(UserShortDto::getId, u -> u));
        return events.stream()
                .map(event -> {
                    final EventShortDto eventDto = eventDtoMapper.mapToShortDto(event, usersInfo.get(event.getInitiatorId()));
                    eventDto.setViews(countViews(event.getId(), event.getCreatedOn(), LocalDateTime.now()));
                    return eventDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto findById(Long userId, Long eventId, Boolean isPublic, HttpServletRequest request) {
        final Event event = findEventById(eventId);

        if (isPublic && !event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        } else if (isPublic) {
            saveView(request);
        } else if (userId != null) {
            userClient.getById(userId);
        }

        final UserShortDto user = userClient.getById(event.getInitiatorId());
        final EventFullDto eventDto = eventDtoMapper.mapToFullDto(event, user);
        eventDto.setViews(countViews(event.getId(), event.getCreatedOn(), LocalDateTime.now()));
        return eventDto;
    }

    @Override
    public EventFullDto findById(Long eventId) {
        final Event event = findEventById(eventId);

        final UserShortDto user = userClient.getById(event.getInitiatorId());
        final EventFullDto eventDto = eventDtoMapper.mapToFullDto(event, user);
        eventDto.setViews(countViews(event.getId(), event.getCreatedOn(), LocalDateTime.now()));
        return eventDto;
    }

    @Override
    public EventFullDto updateByPrivate(Long userId, Long eventId, UpdateEventUserRequest eventDto) {
        final UserShortDto user = userClient.getById(userId);
        final Event event = findEventById(eventId);

        validateUser(event.getInitiatorId(), user);
        validateEventDate(eventDto.getEventDate());
        validateStatusForPrivate(event.getState(), eventDto.getStateAction());

        final Category category = findCategoryById(eventDto.getCategory());
        final Location location = saveLocation(eventDto.getLocation());
        eventDtoMapper.updateFromDto(event, eventDto);

        final Event updatedEvent = eventRepository.save(event);

        final EventFullDto updatedEventDto = eventDtoMapper.mapToFullDto(updatedEvent, user);
        updatedEventDto.setViews(countViews(updatedEvent.getId(), updatedEvent.getCreatedOn(), LocalDateTime.now()));
        return updatedEventDto;
    }

    @Override
    public EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest eventDto) {
        final Event event = findEventById(eventId);

        validateEventDateForAdmin(eventDto.getEventDate() == null ? event.getEventDate() : LocalDateTime.parse(eventDto.getEventDate(), formatter), eventDto.getStateAction());
        validateStatusForAdmin(event.getState(), eventDto.getStateAction());

        final Category category = findCategoryById(eventDto.getCategory());
        final Location location = saveLocation(eventDto.getLocation());
        eventDtoMapper.updateFromDto(event, eventDto);
        if (eventDto.getStateAction() != null && eventDto.getStateAction().equals(EventStateAction.PUBLISH_EVENT)) {
            event.setPublishedOn(LocalDateTime.now());
        }

        final Event updatedEvent = eventRepository.save(event);
        final UserShortDto user = userClient.getById(updatedEvent.getInitiatorId());

        final EventFullDto updatedEventDto = eventDtoMapper.mapToFullDto(updatedEvent, user);
        updatedEventDto.setViews(countViews(updatedEvent.getId(), updatedEvent.getCreatedOn(), LocalDateTime.now()));
        return updatedEventDto;
    }

    @Override
    public void updateEventConfirmedRequests(Long eventId, Long confirmedRequests) {
        final Event event = findEventById(eventId);
        event.setConfirmedRequests(confirmedRequests);
        eventRepository.save(event);
    }

    private void validateUser(Long userId, UserShortDto initiator) {
        if (!initiator.getId().equals(userId)) {
            throw new NotFoundException("Trying to change information not from initiator of event");
        }
    }

    private void validateEventDate(String eventDate) {
        if (eventDate != null && LocalDateTime.parse(eventDate, formatter).isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IncorrectRequestException("Event date should be early than 2 hours than current moment " + eventDate + " " + LocalDateTime.parse(eventDate, formatter));
        }
    }

    private void validateEventDateForAdmin(LocalDateTime eventDate, EventStateAction stateAction) {
        if (eventDate != null && eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IncorrectRequestException("Event date should be early than 2 hours than current moment");
        }
        if (stateAction != null && stateAction.equals(EventStateAction.PUBLISH_EVENT) && eventDate.isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ForbiddenException("Event date should be early than 1 hours than publish moment");
        }
    }

    private void validateStatusForPrivate(EventState state, EventStateAction stateAction) {
        if (state.equals(EventState.PUBLISHED)) {
            throw new ConflictException("Can't change event not cancelled or in moderation");
        }
        switch (stateAction) {
            case null:
            case EventStateAction.CANCEL_REVIEW:
            case EventStateAction.SEND_TO_REVIEW:
                return;
            default:
                throw new ForbiddenException("Unknown state action");
        }
    }

    private void validateStatusForAdmin(EventState state, EventStateAction stateAction) {
        if (!state.equals(EventState.PENDING) && stateAction.equals(EventStateAction.PUBLISH_EVENT)) {
            throw new ConflictException("Can't publish not pending event");
        }
        if (state.equals(EventState.PUBLISHED) && stateAction.equals(EventStateAction.REJECT_EVENT)) {
            throw new ConflictException("Can't reject already published event");
        }
        if (stateAction != null && !stateAction.equals(EventStateAction.REJECT_EVENT) && !stateAction.equals(EventStateAction.PUBLISH_EVENT)) {
            throw new ForbiddenException("Unknown state action");
        }
    }

    private Category findCategoryById(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        final CategoryDto categoryDto = categoryService.findById(categoryId);
        final Category category = categoryDtoMapper.mapFromDto(categoryDto);
        return category;
    }

    private Location saveLocation(LocationDto locationDto) {
        if (locationDto == null) {
            return null;
        }
        final LocationDto createdLocationDto = locationService.create(locationDto);
        final Location location = locationDtoMapper.mapFromDto(createdLocationDto);
        return location;
    }

    private Event findEventById(Long eventId) {
        final Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id=" + eventId + " was not found")
        );

        return event;
    }

    private void saveView(HttpServletRequest request) {
        final String url = UriComponentsBuilder.newInstance()
                .scheme(STATS_SERVICE_SCHEME)
                .host(STATS_SERVICE_NAME)
                .path("/hit")
                .toUriString();

        final NewEventViewDto viewDto = NewEventViewDto.builder()
                .app(APP_NAME)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now().format(formatter))
                .build();

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        final HttpEntity<Object> requestBody = new HttpEntity<>(viewDto, headers);

        log.info("Отправка запроса в сервис статистики: {}", url);
        log.info("Тело запроса: {}", requestBody);

        try {
            ResponseEntity<Object> response = restTemplate.postForEntity(url, requestBody, Object.class);

            if (response.getStatusCode() == HttpStatus.CREATED) {
                log.info("Просмотр успешно записанного события.");
            } else {
                log.error("Ошибка при сохранении просмотра. Код ответа: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage(), e);
        }
    }

    private Long countViews(Long eventId, LocalDateTime start, LocalDateTime end) {
        final List<String> uris = List.of(
                "/events/" + eventId
        );
        final String url = UriComponentsBuilder.newInstance()
                .scheme(STATS_SERVICE_SCHEME)
                .host(STATS_SERVICE_NAME)
                .path("/stats")
                .queryParam("start", start.format(formatter))
                .queryParam("end", end.format(formatter))
                .queryParam("uris", uris)
                .queryParam("unique", "true")
                .toUriString();

        log.info(url);

        final ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        log.info(response.getStatusCode().toString());

        if (response.getStatusCode() != HttpStatus.OK) {
            return 0L;
        }

        long sumOfViews = 0L;
        JsonArray jsonArray = (JsonArray) JsonParser.parseString(response.getBody());
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = (JsonObject) jsonArray.get(i);
            sumOfViews += jsonObject.get("hits").getAsLong();
        }

        return sumOfViews;
    }
}
