package ru.practicum.service.event;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.*;

import java.util.List;

public interface EventService {
    List<EventShortDto> getUserEvents(Long userId, int from, int size);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getUserEvent(Long userId, Long eventId);

    EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventRequest updateEventRequest);

    List<EventFullDto> searchEvents(List<Long> users, List<String> states, List<Long> categories,
                                    String rangeStart, String rangeEnd, int from, int size);

    EventFullDto updateEventByAdmin(Long eventId, AdminUpdateEventRequest updateEventRequest);

    List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, String rangeStart,
                                  String rangeEnd, Boolean onlyAvailable, String sort, int from, int size,
                                  HttpServletRequest request);

    EventFullDto getEventById(Long id, HttpServletRequest request);

    Event getEventEntityById(Long eventId);

    List<Event> getEventsByIds(List<Long> eventIds);
}
