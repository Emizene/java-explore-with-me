package ru.practicum.service.event;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.*;
import ru.practicum.emuns.EventState;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.request.RequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final RequestService requestService;
    private final StatsClient statsClient;

    @Override
    public List<EventShortDto> getUserEvents(Long userId, int from, int size) {
        checkUserExists(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);
        return events.stream()
                .map(eventMapper::toShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        Category category = categoryRepository.findById(newEventDto.categoryId())
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + newEventDto.categoryId()));

        Event event = eventMapper.toEntity(newEventDto);
        event.setInitiator(user);
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);

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
    public List<EventFullDto> searchEvents(List<Long> users, List<String> states, List<Long> categories,
                                           String rangeStart, String rangeEnd, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<EventState> eventStates = parseEventStates(states);
        LocalDateTime start = parseDateTime(rangeStart);
        LocalDateTime end = parseDateTime(rangeEnd);

        List<Event> events = eventRepository.searchEvents(users, eventStates, categories, start, end, pageable);
        return events.stream()
                .map(eventMapper::toFullDto)
                .collect(Collectors.toList());
    }

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
                                         String rangeEnd, Boolean onlyAvailable, String sort, int from, int size,
                                         HttpServletRequest request) {
        Pageable pageable = PageRequest.of(from / size, size);
        LocalDateTime start = parseDateTime(rangeStart);
        LocalDateTime end = parseDateTime(rangeEnd);

        List<Event> events = eventRepository.findPublicEvents(text, categories, paid, start, end, onlyAvailable, pageable);

        EndpointHit hit = new EndpointHit(
                "ewm-main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
        );

        statsClient.saveHit(hit);

        return events.stream()
                .map(eventMapper::toShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventById(Long id, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Published event not found with id: " + id));

        EndpointHit hit = new EndpointHit(
                "ewm-main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
        );

        statsClient.saveHit(hit);

        return eventMapper.toFullDto(event);
    }

    @Override
    public Event getEventEntityById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found with id: " + eventId));
    }

    @Override
    public List<Event> getEventsByIds(List<Long> eventIds) {
        return eventRepository.findAllById(eventIds);
    }

    private void updateEventFields(Event event, Object updateRequest) {
        if (updateRequest instanceof UpdateEventRequest userRequest) {
            if (userRequest.getAnnotation() != null) event.setAnnotation(userRequest.getAnnotation());
            if (userRequest.getCategoryId() != null) {
                Category category = categoryRepository.findById(userRequest.getCategoryId())
                        .orElseThrow(() -> new NotFoundException("Category not found"));
                event.setCategory(category);
            }
            if (userRequest.getDescription() != null) event.setDescription(userRequest.getDescription());
            if (userRequest.getEventDate() != null) event.setEventDate(userRequest.getEventDate());
            if (userRequest.getPaid() != null) event.setPaid(userRequest.getPaid());
            if (userRequest.getParticipantLimit() != null) event.setParticipantLimit(userRequest.getParticipantLimit());
            if (userRequest.getTitle() != null) event.setTitle(userRequest.getTitle());
        }
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
                .collect(Collectors.toList());
    }

    private LocalDateTime parseDateTime(String dateTimeString) {
        if (dateTimeString == null) return null;
        return LocalDateTime.parse(dateTimeString, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}