package ru.practicum.service.event;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHit;
import ru.practicum.StatsClient;
import ru.practicum.ViewStats;
import ru.practicum.dto.AdminUpdateEventRequest;
import ru.practicum.dto.UpdateEventRequest;
import ru.practicum.emuns.EventSort;
import ru.practicum.emuns.EventState;
import ru.practicum.eventDto.EventFullDto;
import ru.practicum.eventDto.EventShortDto;
import ru.practicum.eventDto.NewEventDto;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.repository.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final EventMapper eventMapper;
    private final StatsClient statsClient;

    @Override
    public List<EventShortDto> getUserEvents(Long userId, int from, int size) {
        checkUserExists(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);
        return events.stream()
                .map(eventMapper::toShortDto)
                .toList();
    }

    @Transactional
    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        correctTimeCreated(newEventDto.eventDate());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        Category category = categoryRepository.findById(newEventDto.category())
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + newEventDto.category()));

        Event event = eventMapper.toEntity(newEventDto);
        event.setInitiator(user);
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);

        locationRepository.save(event.getLocation());
        Event savedEvent = eventRepository.save(event);
        return eventMapper.toFullDto(savedEvent);
    }

    @Override
    public EventFullDto getUserEvent(Long userId, Long eventId) {
        checkUserExists(userId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event not found with id: " + eventId + " for user: " + userId));
        return eventMapper.toFullDto(event);
    }

    @Transactional
    @Override
    public EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventRequest updateEventRequest) {
        checkUserExists(userId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event not found with id: " + eventId + " for user: " + userId));

        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        updateEventFields(event, updateEventRequest);

        if (updateEventRequest.getStateAction() != null) {
            handleUserStateAction(event, updateEventRequest.getStateAction());
        }

        Event updatedEvent = eventRepository.save(event);
        return eventMapper.toFullDto(updatedEvent);
    }

    @Override
    @Transactional
    public List<EventFullDto> searchEvents(List<Long> users, List<String> states, List<Long> categories,
                                           String rangeStart, String rangeEnd, int from, int size) {
        Pageable pageable = PageRequest.of(from * size, size);
        List<EventState> eventStates = parseEventStates(states);
        LocalDateTime start = parseDateTime(rangeStart);
        LocalDateTime end = parseDateTime(rangeEnd);

        List<Event> events = eventRepository.searchEvents(users,
                eventStates,
                categories,
                start != null ? start : LocalDateTime.of(1970, 1, 1, 0, 0),
                end != null ? end : LocalDateTime.of(2970, 1, 1, 0, 0),
                pageable);
        return events.stream()
                .map(eventMapper::toFullDto)
                .toList();
    }

    @Transactional
    @Override
    public EventFullDto updateEventByAdmin(Long eventId, AdminUpdateEventRequest updateEventRequest) {
        Event event = getEventEntityById(eventId);

        updateEventFields(event, updateEventRequest);

        if (updateEventRequest.getStateAction() != null) {
            handleAdminStateAction(event, updateEventRequest.getStateAction());
        }

        Event updatedEvent = eventRepository.save(event);
        return eventMapper.toFullDto(updatedEvent);
    }

    @Override
    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, String rangeStart,
                                         String rangeEnd, Boolean onlyAvailable, EventSort sort, int from, int size,
                                         HttpServletRequest request) {

        LocalDateTime start = parseDateTime(rangeStart);
        LocalDateTime end = parseDateTime(rangeEnd);

        if (start != null && end != null && start.isAfter(end)) {
            throw new BadRequestException("START can't ba after END.");
        }

        Pageable pageable = PageRequest.of(from / size, size);

        Page<Event> page;

        if (start == null || end == null) {
            page = eventRepository.searchEventCurrentTime(text, categories, paid, pageable);
        } else {
            page = eventRepository.searchEvent(text, categories, start, end, paid, pageable);
        }

        List<Event> events = page.getContent();

        List<Long> eventIds = events.stream().map(Event::getId).toList();
        List<Object[]> counts = requestRepository.countConfirmedRequestsByEventIds(eventIds);

        Map<Long, Long> confirmedMap = counts.stream()
                .collect(Collectors.toMap(
                        arr -> (Long) arr[0],
                        arr -> (Long) arr[1]
                ));

        if (Boolean.TRUE.equals(onlyAvailable)) {
            events.removeIf(event -> {
                Long confirmedCount = confirmedMap.getOrDefault(event.getId(), 0L);
                return confirmedCount >= event.getParticipantLimit() && event.getParticipantLimit() != 0;
            });
        }

        Map<Long, Long> viewsStats = getViewsStatistics(events);

        EndpointHit hitDto = new EndpointHit(
                "ewm-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
        );
        statsClient.saveHit(hitDto);

        List<EventShortDto> result = new ArrayList<>(events.stream()
                .map(event -> {
                    EventShortDto dto = eventMapper.toShortDto(event);
                    return dto.withStats(
                            confirmedMap.getOrDefault(event.getId(), 0L).intValue(),
                            Math.toIntExact(viewsStats.getOrDefault(event.getId(), 0L))
                    );
                })
                .toList());

        if (sort == null || sort.equals(EventSort.EVENT_DATE)) {
            result.sort(Comparator.comparing(EventShortDto::eventDate));
        } else if (sort.equals(EventSort.VIEWS)) {
            result.sort(Comparator.comparing(EventShortDto::views).reversed());
        }

        return result;
    }

    private Map<Long, Long> getViewsStatistics(List<Event> events) {
        if (events.isEmpty()) {
            return Collections.emptyMap();
        }

        LocalDateTime startDate = events.stream()
                .map(Event::getCreatedOn)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now().minusYears(1));

        List<String> uris = events.stream()
                .map(event -> String.format("/events/%d", event.getId()))
                .toList();

        try {
            List<ViewStats> stats = statsClient.getStats(
                    startDate,
                    LocalDateTime.now(),
                    uris,
                    true
            );

            if (stats != null) {
                return stats.stream()
                        .collect(Collectors.toMap(
                                statsItem -> extractEventIdFromUri(statsItem.uri()),
                                ViewStats::hits
                        ));
            }
        } catch (Exception e) {
            log.warn("Failed to get views statistics", e);
        }

        return Collections.emptyMap();
    }

    @Override
    @Transactional
    public EventFullDto getEventById(Long id, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Published event not found with id: " + id));

        String ipAddress = getClientIpAddress(request);

        EndpointHit hit = new EndpointHit(
                "ewm-main-service",
                request.getRequestURI(),
                ipAddress,
                LocalDateTime.now()
        );
        statsClient.saveHit(hit);

        if (!event.getViews().contains(ipAddress)) {
            event.getViews().add(ipAddress);
            eventRepository.save(event);
        }

        return eventMapper.toFullDto(event);
    }

    @Override
    public Event getEventEntityById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found with id: " + eventId));
    }

    private void updateEventFields(Event event, UpdateEventRequest updateRequest) {
        if (updateRequest.getAnnotation() != null) event.setAnnotation(updateRequest.getAnnotation());
        if (updateRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(updateRequest.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            event.setCategory(category);
        }
        if (updateRequest.getDescription() != null) event.setDescription(updateRequest.getDescription());
        if (updateRequest.getEventDate() != null) event.setEventDate(updateRequest.getEventDate());
        if (updateRequest.getPaid() != null) event.setPaid(updateRequest.getPaid());
        if (updateRequest.getParticipantLimit() != null) event.setParticipantLimit(updateRequest.getParticipantLimit());
        if (updateRequest.getTitle() != null) event.setTitle(updateRequest.getTitle());
    }

    private void updateEventFields(Event event, AdminUpdateEventRequest updateRequest) {
        if (updateRequest.getAnnotation() != null) event.setAnnotation(updateRequest.getAnnotation());
        if (updateRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(updateRequest.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            event.setCategory(category);
        }
        if (updateRequest.getDescription() != null) event.setDescription(updateRequest.getDescription());

        if (updateRequest.getEventDate() != null) {
            LocalDateTime newEventDate = updateRequest.getEventDate();

            if (event.getState() == EventState.PENDING) {
                LocalDateTime minimumAllowedDate = LocalDateTime.now().plusHours(1);
                if (newEventDate.isBefore(minimumAllowedDate)) {
                    throw new ConflictException("Event start date must be at least 1 hour after publication");
                }
            }

            event.setEventDate(newEventDate);
        }

        if (updateRequest.getPaid() != null) event.setPaid(updateRequest.getPaid());
        if (updateRequest.getParticipantLimit() != null) event.setParticipantLimit(updateRequest.getParticipantLimit());
        if (updateRequest.getTitle() != null) event.setTitle(updateRequest.getTitle());
    }

    private void handleUserStateAction(Event event, String stateAction) {
        switch (stateAction) {
            case "SEND_TO_REVIEW":
                event.setState(EventState.PENDING);
                break;
            case "CANCEL_REVIEW":
                event.setState(EventState.CANCELED);
                break;
            default:
                throw new ValidationException("Invalid state action: " + stateAction);
        }
    }

    private void handleAdminStateAction(Event event, String stateAction) {
        switch (stateAction) {
            case "PUBLISH_EVENT":
                if (event.getState() != EventState.PENDING) {
                    throw new ConflictException("Event must be pending to be published");
                }
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
                break;
            case "REJECT_EVENT":
                if (event.getState() == EventState.PUBLISHED) {
                    throw new ConflictException("Cannot reject published event");
                }
                event.setState(EventState.CANCELED);
                break;
            default:
                throw new ValidationException("Invalid state action: " + stateAction);
        }
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found with id: " + userId);
        }
    }

    private List<EventState> parseEventStates(List<String> stateStrings) {
        if (stateStrings == null) return null;
        return stateStrings.stream()
                .map(EventState::valueOf)
                .toList();
    }

    private LocalDateTime parseDateTime(String dateTimeString) {
        if (dateTimeString == null) return null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(dateTimeString, formatter);
    }

    private Long extractEventIdFromUri(String uri) {
        try {
            String[] parts = uri.split("/");
            return Long.parseLong(parts[parts.length - 1]);
        } catch (Exception e) {
            return -1L;
        }
    }

    private void correctTimeCreated(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Field: eventDate. Error: must contain a date " +
                    "that has not yet occurred. Value: " + eventDate);
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

}